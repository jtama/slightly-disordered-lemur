package com.onepoint.yap;

import com.onepoint.yap.RandomKillRequestStatus.State;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.DeleteControl;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.EventSourceManager;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class RandomKillController implements ResourceController<RandomKillRequest> {

    Logger logger;
    KubernetesClient client;

    public RandomKillController(Logger logger, KubernetesClient client) {
        super();
        this.logger = logger;
        this.client = client;
    }

    @Override
    public DeleteControl deleteResource(RandomKillRequest resource, Context<RandomKillRequest> context) {
        return DeleteControl.DEFAULT_DELETE;
    }

    @Override
    public UpdateControl<RandomKillRequest> createOrUpdateResource(RandomKillRequest rkr, Context<RandomKillRequest> context) {
        var spec = rkr.getSpec();
        var status = rkr.getStatus();
        if (status != null && State.CREATED == status.state()) {
            return UpdateControl.noUpdate();
        }
        try {
            if (client.namespaces().withName(spec.namespace()).get() == null) {
                rkr.setStatus(RandomKillRequestStatus.from(State.ERROR, "No %s namespace exists in cluster".formatted(spec.namespace())));
                return UpdateControl.updateStatusSubResource(rkr);
            }
            status = Optional.ofNullable(rkr.getMetadata().getAnnotations().get("pod-name"))
                    .map(podName -> controlUpdated(podName, rkr))
                    .orElseGet(() -> processCreated(rkr));
        } catch (Exception e) {
            logger.error("Error querying API", e);
            status = RandomKillRequestStatus.from(State.ERROR, "Error querying API: " + e.getMessage());
        }
        rkr.setStatus(status);
        return UpdateControl.updateCustomResourceAndStatus(rkr);
    }

    @Override
    public void init(EventSourceManager eventSourceManager) {
        ResourceController.super.init(eventSourceManager);
    }

    private RandomKillRequestStatus controlUpdated(String podName, RandomKillRequest rkr) {
        Pod pod = client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName).get();
        if (pod == null) {
            return RandomKillRequestStatus.from(State.DONE, "Pod has been taken care of");
        }
        deleteIfNeeded(rkr, pod);
        return RandomKillRequestStatus.from(State.PROCESSING, pod.getStatus().getMessage());
    }

    private RandomKillRequestStatus processCreated(RandomKillRequest rkr) {
        List<Pod> podsInNamespace = client.pods().inNamespace(rkr.getSpec().namespace()).list().getItems();
        if (podsInNamespace.size() < 2) {
            return RandomKillRequestStatus.from(State.DONE,"Nothing to do.");
        }
        var pod = podsInNamespace.get(RandomGenerator.getDefault().nextInt(0, podsInNamespace.size() - 1));
        rkr.getMetadata().getAnnotations().put("pod-name", pod.getMetadata().getName());
        rkr.getMetadata().getAnnotations().put("pod-uid", pod.getMetadata().getUid());
        return deleteIfNeeded(rkr, pod);
    }

    private RandomKillRequestStatus deleteIfNeeded(RandomKillRequest rkr, Pod pod) {
        if (!rkr.getSpec().targetOnly()) {
            client.pods().delete(pod);
            return RandomKillRequestStatus.from(State.DONE, "RandomKillRequest %s killed".formatted(pod.getMetadata().getName()));
        }
        return RandomKillRequestStatus.from(State.DONE, "RandomKillRequest target is %s".formatted(pod.getMetadata().getName()));
    }
}
