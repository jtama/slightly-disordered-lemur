package com.onepoint.sdl.rk;

import com.onepoint.sdl.r.RandomRequestStatus;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.events.v1.EventBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

class KillPodWatcher implements Watcher<Pod> {
    private final static DateTimeFormatter microTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.000000'Z'")
        .withZone(ZoneId.systemDefault());

    private RandomKillRequest rkr;
    private final String podName;
    private final long start;
    private final Logger logger;
    private KubernetesClient client;
    private String controllerName;

    public KillPodWatcher(KubernetesClient client, Logger logger, RandomKillRequest rkr, String podName, String controllerName) {
        this.client = client;
        this.logger = logger;
        this.rkr = rkr;
        this.podName = podName;
        this.controllerName = controllerName;
        this.start = System.currentTimeMillis();
        logger.debugf("Starting KillPodWatcher '%s'.", this.toString());
    }

    @Override
    public void eventReceived(Action action, Pod pod) {
        logger.debugf("Action triggered on pod %s : %s", pod.getMetadata().getName(), action.toString());
        if (!action.equals(Action.DELETED))
            return;
        rkr = client.resources(rkr.getClass())
            .inNamespace(rkr.getMetadata().getNamespace())
            .withName(rkr.getMetadata().getName())
            .editStatus(item -> {
                item.setStatus(RandomRequestStatus.from(RandomRequestStatus.State.DONE, rkr.getDoneMessage(podName)));
                return item;
            });

        logger.debugf("Adding kill event for rkr '%s' with version '%s'", rkr.getMetadata().getName(), rkr.getMetadata().getResourceVersion());

        var event = client.events().v1().events().inNamespace(rkr.getMetadata().getNamespace()).create(
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
                .withReason("Killed")
                .withReportingController(controllerName)
                .withReportingInstance(System.getenv("HOSTNAME"))
                .withAction(rkr.getCRDName())
                .withNewEventTime(microTimeFormatter.format(ZonedDateTime.now()))
                .withNote("%s in %s ms.".formatted(rkr.getDoneMessage(podName), (System.currentTimeMillis() - start)))
                .withType("Normal")
                .build()
        );
        logger.debugf("Event '%s' created with action '%s'", event.getMetadata().getName(), event.getAction());
    }

    @Override
    public void onClose() {
        logger.debugf("Closing KillPodWatcher '%s'.", this.toString());
    }

    @Override
    public void onClose(WatcherException cause) {
        logger.error(cause.asClientException());
    }
}
