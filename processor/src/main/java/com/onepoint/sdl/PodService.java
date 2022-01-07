package com.onepoint.sdl;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniEmitter;
import okhttp3.Response;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.random.RandomGenerator;

@ApplicationScoped
public class PodService {

    private Logger logger;
    private KubernetesClient client;
    private Configuration config;

    public PodService(Logger logger, KubernetesClient client, Configuration config) {
        this.logger = logger;
        this.client = client;
        this.config = config;
    }

    public Uni<String> targetPod() {
        return Uni.createFrom()
            .item(() -> client.pods().inNamespace(config.namespace())
                .withNewFilter()
                .withoutField("metadate.name", "sld-worker")
                .endFilter()
                .list().getItems(), this::getRandomPod);
    }

    private String getRandomPod(List<Pod> pods) {
        if (pods.size() == 0) {
            logger.info("No pod available for targeting.");
            return "";
        }
        return pods.get(RandomGenerator.getDefault().nextInt(0, pods.size())).getMetadata().getName();
    }

    public Uni<Boolean> killPod(String podName) {
        return Uni.createFrom()
            .item(() -> client.pods().inNamespace(config.namespace()).withName(podName).delete())
            .onItem()
            .invoke(killed -> {
                if (killed) {
                    logger.infof("Pod %s was killed with success.", podName);
                } else {
                    logger.infof("Pod %s is already gone.", podName);
                }
            });
    }

    public Uni<Boolean> invadePod(String podName) {
        return Uni.createFrom().emitter(em -> {
            client.pods().inNamespace(config.namespace()).withName(podName)
                .writingOutput(System.out)
                .usingListener(new LogListener(em))
                .exec("sh", "-c", "echo \\\"%s\\\" > /tmp/invasion.txt".formatted(Invasion.BENDER.toString()).replace("\"", "\\\"").replace("'", "\\'"));
        });
    }


    private class LogListener implements ExecListener {

        private UniEmitter<? super Boolean> em;

        public LogListener(UniEmitter<? super Boolean> em) {
            this.em = em;
        }

        @Override
        public void onOpen(Response response) {
            logger.info("Beginning invasion:");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            logger.error("Damn, invasion failed", t);
            em.fail(t);
        }

        @Override
        public void onClose(int code, String reason) {
            logger.info("Invasion successfull");
            em.complete(code == 1000);
        }
    }
}
