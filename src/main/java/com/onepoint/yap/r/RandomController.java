package com.onepoint.yap.r;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.DeleteControl;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.EventSourceManager;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.random.RandomGenerator;


public abstract class RandomController<T extends RandomRequest> implements ResourceController<T> {


    private final static Set<RandomRequestStatus.State> NO_UPDATE_STATES = Set.of(RandomRequestStatus.State.DONE, RandomRequestStatus.State.CREATED);

    private Logger logger;
    private KubernetesClient client;
    private Config config;

    public RandomController() {
    }

    public RandomController(Logger logger, KubernetesClient client, Config config) {
        super();
        this.logger = logger;
        this.client = client;
        this.config = config;

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

    @Override
    public void init(EventSourceManager eventSourceManager) {
        ResourceController.super.init(eventSourceManager);
    }

    private RandomRequestStatus controlUpdated(String podName, T rkr) {
        PodResource<Pod> pod = client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName);
        if (pod.get() == null && !rkr.getSpec().targetOnly()) {
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Pod has been taken care of.");
        }
        return invadeIfNeeded(rkr, podName);
    }

    private RandomRequestStatus processCreation(T rkr) {
        List<Pod> podsInNamespace = client.pods().inNamespace(rkr.getSpec().namespace()).list().getItems();
        if (podsInNamespace.size() < getMinPod(rkr)) {
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Nothing to do.");
        }
        var pod = podsInNamespace.get(podsInNamespace.size() == 1 ? 0 : RandomGenerator.getDefault().nextInt(0, podsInNamespace.size() - 1));
        rkr.getMetadata().getAnnotations().put("pod-name", pod.getMetadata().getName());
        rkr.getMetadata().getAnnotations().put("pod-uid", pod.getMetadata().getUid());
        return invadeIfNeeded(rkr, pod.getMetadata().getName());
    }

    private int getMinPod(T rkr) {
        return config.getNamespace().equals(rkr.getMetadata().getNamespace()) ? 2 : 1;
    }

    private RandomRequestStatus invadeIfNeeded(T rkr, String podName) {
        if (!rkr.getSpec().targetOnly()) {
            return process(rkr, podName);
        }
        return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "Slightly disordered lemure target is '%s' 🎯".formatted(podName));
    }

    public KubernetesClient getClient() {
        return client;
    }

    public Logger getLogger() {
        return logger;
    }

    protected abstract RandomRequestStatus process(T rkr, String podName);
}
