package com.onepoint.sdl.ri;

import com.onepoint.sdl.r.RandomController;
import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.Controller;
import org.jboss.logging.Logger;

@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class RandomInvasionController extends RandomController<RandomInvasionRequest> {


    public RandomInvasionController(Logger logger, KubernetesClient client, Config config, WorkerClientFactory workerClientFactory) {
        super(logger, client, config, workerClientFactory);
    }


    @Override
    protected void process(RandomInvasionRequest rkr, String podName) {
        getWorkerClientFactory().getWorkerForNamespace(rkr.getMetadata().getNamespace()).invade(podName);
    }

    @Override
    protected String getDoneMessage(String podName) {
        return "Pod %s has been invaded. \uD83C\uDFAF".formatted(podName);
    }


}
