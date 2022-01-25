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

    @Override
    protected String controllerName() {
        return this.getClass().getSimpleName();
    }

    protected void process(RandomKillRequest rkr, String podName) {
        client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName).watch(new KillPodWatcher(client, logger, rkr, podName, controllerName()));
        getWorkerClientFactory().getWorkerForNamespace(rkr.getSpec().namespace()).kill(podName);
    }
}
