package com.onepoint.sdl.r;

import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.events.v1.EventBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.DeleteControl;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public abstract class RandomController<T extends RandomRequest> implements ResourceController<T> {


    private final static Set<RandomRequestStatus.State> NO_UPDATE_STATES = Set.of(RandomRequestStatus.State.DONE, RandomRequestStatus.State.CREATED);
    private final static DateTimeFormatter microTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.000000'Z'")
        .withZone(ZoneId.systemDefault());

    protected Logger logger;
    protected KubernetesClient client;
    private WorkerClientFactory workerClientFactory;

    public RandomController() {
    }

    public RandomController(Logger logger, KubernetesClient client, Config config, WorkerClientFactory workerClientFactory) {
        super();
        this.logger = logger;
        this.client = client;
        this.workerClientFactory = workerClientFactory;

        logger.warn(config.getNamespace());
    }

    @Override
    public DeleteControl deleteResource(T resource, Context<T> context) {
        return DeleteControl.DEFAULT_DELETE;
    }

    @Override
    public UpdateControl<T> createOrUpdateResource(T rkr, Context<T> context) {
        var spec = rkr.getSpec();
        var status = rkr.getStatus();
        if (status != null && NO_UPDATE_STATES.contains(status.state())) {
            return UpdateControl.noUpdate();
        }
        try {
            if (client.namespaces().withName(spec.namespace()).get() == null) {
                rkr.setStatus(RandomRequestStatus.from(RandomRequestStatus.State.ERROR, "No %s namespace exists in cluster".formatted(spec.namespace())));
                return UpdateControl.updateStatusSubResource(rkr);
            }
            status = Optional.ofNullable(rkr.getMetadata().getAnnotations().get("pod-name"))
                .map(podName -> controlUpdated(podName, rkr))
                .orElseGet(() -> processCreation(rkr));
        } catch (Exception e) {
            logger.error("Error querying API", e);
            status = RandomRequestStatus.from(RandomRequestStatus.State.ERROR, "Error querying API: " + e.getMessage());
        }
        rkr.setStatus(status);
        return UpdateControl.updateCustomResourceAndStatus(rkr);
    }

    private RandomRequestStatus controlUpdated(String podName, T rkr) {
        PodResource<Pod> pod = client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName);
        if (pod.get() == null && !rkr.getSpec().targetOnly()) {
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Pod has been taken care of.");
        }
        return processIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processCreation(T rkr) {
        var start = System.currentTimeMillis();
        String podName = workerClientFactory.getWorkerForNamespace(rkr.getSpec().namespace()).target();
        if (podName.isBlank()) {
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Nothing to do.");
        }
        rkr.getMetadata().getAnnotations().put("pod-name", podName);
        client.events().v1().events().inNamespace(rkr.getMetadata().getNamespace()).createOrReplace(
            new EventBuilder()
                .withNewMetadata()
                .withName("%s.%s".formatted(podName, UUID.randomUUID().toString()))
                .endMetadata()
                .withRegarding(new ObjectReferenceBuilder()
                    .withName(rkr.getMetadata().getName())
                    .withNamespace(rkr.getMetadata().getNamespace())
                    .withApiVersion(rkr.getApiVersion())
                    .withUid(rkr.getMetadata().getUid())
                    .withResourceVersion(rkr.getMetadata().getResourceVersion())
                    .withKind(rkr.getKind())
                    .build())
                .withReason("targeted")
                .withAction(rkr.getCRDName())
                .withReportingController(controllerName())
                .withReportingInstance(System.getenv("HOSTNAME"))
                .withNewEventTime(microTimeFormatter.format(ZonedDateTime.now()))
                .withNote("Pod has been targeted in %s ms. 🎯".formatted("" + (System.currentTimeMillis() - start)))
                .withType("Normal")
                .build()
        );
        return processIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processIfNeeded(T rkr, String podName) {
        if (!rkr.getSpec().targetOnly()) {
            process(rkr, podName);
        }
        return RandomRequestStatus.from(RandomRequestStatus.State.PROCESSING, "Slightly disordered lemure target is '%s' 🎯.".formatted(podName));
    }

    protected abstract String controllerName();

    protected abstract void process(T rkr, String podName);

    protected WorkerClientFactory getWorkerClientFactory() {
        return workerClientFactory;
    }

}
