package com.onepoint.sdl.r;

import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.events.v1.EventBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.DeleteControl;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import org.jboss.logging.Logger;

import java.util.Optional;
import java.util.Set;


public abstract class RandomController<T extends RandomRequest> implements ResourceController<T> {


    private final static Set<RandomRequestStatus.State> NO_UPDATE_STATES = Set.of(RandomRequestStatus.State.DONE, RandomRequestStatus.State.CREATED);

    private Logger logger;
    private KubernetesClient client;
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
        if (podName.isBlank()){
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Nothing to do.");
        }
        rkr.getMetadata().getAnnotations().put("pod-name", podName);
        client.events().v1().events().inNamespace(rkr.getMetadata().getNamespace()).createOrReplace(
            new EventBuilder()
                .withNewMetadata()
                    .withName("%s-%s".formatted(rkr.getCRDName(),podName))
                .endMetadata()
                .withRegarding(new ObjectReferenceBuilder()
                    .withName(podName)
                    .build())
                .withAction("targeted")
                .withNote("Pod has been targeted in %s ms. 🎯".formatted("" + (System.currentTimeMillis() - start)))
                .build()
        );
        return processIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processIfNeeded(T rkr, String podName) {
        var start = System.currentTimeMillis();
        client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName).watch(new Watcher<>() {
            @Override
            public void eventReceived(Action action, Pod pod) {
                logger.infof("Action triggered on pod %s : %s", pod.getMetadata().getName(), action.toString());
                client.resource(rkr).inNamespace(rkr.getMetadata().getNamespace())
                        .edit(this::setRkrStatusDone);
                client.events().v1().events().inNamespace(pod.getMetadata().getNamespace()).createOrReplace(
                    new EventBuilder()
                        .withNewMetadata()
                        .withName("%s-%s".formatted(rkr.getCRDName(),podName))
                        .endMetadata()
                        .withRegarding(new ObjectReferenceBuilder()
                            .withName(podName)
                            .build())
                        .withAction(rkr.getCRDName())
                        .withNote("% in %s ms.".formatted(getDoneMessage(podName), (System.currentTimeMillis() - start)))
                        .build()
                );
            }

            private T setRkrStatusDone(T rkr) {
                rkr.setStatus(RandomRequestStatus.from(RandomRequestStatus.State.DONE, getDoneMessage(podName)));
                return rkr;
            }

            @Override
            public void onClose(WatcherException cause) {
                logger.error(cause.asClientException());
            }
        });
        if (!rkr.getSpec().targetOnly()) {
            process(rkr, podName);
        }
        return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Slightly disordered lemure target is '%s' 🎯.".formatted(podName));
    }

    protected abstract void process(T rkr, String podName);

    protected abstract String getDoneMessage(String podName);

    protected WorkerClientFactory getWorkerClientFactory() {
        return workerClientFactory;
    }
}
