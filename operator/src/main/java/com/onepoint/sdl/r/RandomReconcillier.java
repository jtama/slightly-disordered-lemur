package com.onepoint.sdl.r;

import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.events.v1.EventBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public abstract class RandomReconcillier<T extends RandomRequest> implements Reconciler<T> {


    private final static Set<RandomRequestStatus.State> NO_UPDATE_STATES = Set.of(RandomRequestStatus.State.DONE, RandomRequestStatus.State.CREATED);
    private final static DateTimeFormatter microTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.000000'Z'")
        .withZone(ZoneId.systemDefault());

    protected Logger logger;
    protected KubernetesClient client;
    private WorkerClientFactory workerClientFactory;

    public RandomReconcillier() {
    }

    public RandomReconcillier(Logger logger, KubernetesClient client, Config config, WorkerClientFactory workerClientFactory) {
        super();
        this.logger = logger;
        this.client = client;
        this.workerClientFactory = workerClientFactory;

        logger.warn(config.getNamespace());
    }

    @Override
    public UpdateControl<T> reconcile(T rkr, Context context) {
        var spec = rkr.getSpec();
        var status = rkr.getStatus();
        logger.debugf("Received reconcialation request for '%s' with status '%s'", rkr.getMetadata().getName(), status);
        if (status != null && NO_UPDATE_STATES.contains(status.state())) {
            return UpdateControl.noUpdate();
        }
        try {
            if (client.namespaces().withName(spec.namespace()).get() == null) {
                logger.debugf("No %s namespace exists in cluster", spec.namespace());
                rkr.setStatus(RandomRequestStatus.from(RandomRequestStatus.State.ERROR, "No %s namespace exists in cluster".formatted(spec.namespace())));
                return UpdateControl.updateResourceAndStatus(rkr);
            }
            status = Optional.ofNullable(rkr.getMetadata().getAnnotations().get("pod-name"))
                .map(podName -> controlUpdated(podName, rkr))
                .orElseGet(() -> processCreation(rkr));
        } catch (Exception e) {
            logger.error("Error querying API", e);
            status = RandomRequestStatus.from(RandomRequestStatus.State.ERROR, "Error querying API: " + e.getMessage());
        }
        rkr.setStatus(status);
        return UpdateControl.updateResourceAndStatus(rkr);
    }

    private RandomRequestStatus controlUpdated(String podName, T rkr) {
        PodResource<Pod> pod = client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName);
        if (pod.get() == null && !rkr.getSpec().targetOnly()) {
            logger.debugf("Pod '%s' no longer exists", podName);
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Pod has been taken care of.");
        }
        return processIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processCreation(T rkr) {
        logger.debugf("Targeting '%s' for the first time", rkr.getMetadata().getName());
        var start = System.currentTimeMillis();
        String podName = workerClientFactory.getWorkerForNamespace(rkr.getSpec().namespace()).target();
        if (podName == null || podName.isBlank()) {
            logger.debugf("Processed '%s' for the first time. Nothing to do.", rkr.getMetadata().getName());
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Nothing to do.");
        }

        logger.debugf("Adding annotation and event on '%s'.", rkr.getMetadata().getName());
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
                .withReportingController(controllerName())
                .withReportingInstance(System.getenv("HOSTNAME"))
                .withAction(rkr.getCRDName())
                .withNewEventTime(microTimeFormatter.format(ZonedDateTime.now()))
                .withNote("Pod has been targeted in %s ms. 🎯".formatted("" + (System.currentTimeMillis() - start)))
                .withType("Normal")
                .build()
        );
        return processIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processIfNeeded(T rkr, String podName) {
        if (!rkr.getSpec().targetOnly()) {
            logger.debugf("Asking processing '%s'.", rkr.getMetadata().getName());
            process(rkr, podName);
        } else {
            logger.debugf("Not processing '%s', targeting only.", rkr.getMetadata().getName());
        }
        return RandomRequestStatus.from(RandomRequestStatus.State.PROCESSING, "Slightly disordered lemure target is '%s' 🎯.".formatted(podName));
    }

    protected abstract String controllerName();

    protected abstract void process(T rkr, String podName);

    protected WorkerClientFactory getWorkerClientFactory() {
        return workerClientFactory;
    }

}
