package com.onepoint.sdl.rk;

import com.onepoint.sdl.r.RandomController;
import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.Controller;
import org.jboss.logging.Logger;

@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class RandomKillController extends RandomController<RandomKillRequest> {

    public RandomKillController(Logger logger, KubernetesClient client, Config config, WorkerClientFactory workerClientFactory) {
        super(logger, client, config, workerClientFactory);
    }

    protected void process(RandomKillRequest rkr, String podName) {
        getWorkerClientFactory().getWorkerForNamespace(rkr.getSpec().namespace()).kill(podName);
    }

    @Override
    protected String getDoneMessage(String podName) {
        return "Pod %s has been billed. \uD83D\uDC80".formatted(podName);
    }
}
