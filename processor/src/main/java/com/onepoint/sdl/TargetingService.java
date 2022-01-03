package com.onepoint.sdl;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.random.RandomGenerator;

@ApplicationScoped
public class TargetingService {

    private Logger logger;
    private KubernetesClient client;
    private Configuration config;

    public TargetingService(Logger logger, KubernetesClient client, Configuration config) {
        this.logger = logger;
        this.client = client;
        this.config = config;
    }

    public Uni<String> targetPod() {
        List<Pod> podsInNamespace = client.pods().inNamespace(config.namespace()).list().getItems();
        if (podsInNamespace.size() < 2) {
            return Uni.createFrom().item("");
        }
        var pod = podsInNamespace.get(RandomGenerator.getDefault().nextInt(0, podsInNamespace.size() - 1));
        return Uni.createFrom().item(pod.getMetadata().getName());
    }
}
