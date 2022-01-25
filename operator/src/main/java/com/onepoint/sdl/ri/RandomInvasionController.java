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
    protected String controllerName() {
        return getClass().getSimpleName();
    }


    @Override
    protected void process(RandomInvasionRequest rkr, String podName) {
        client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName).watch(new InvadePodWatcher(client, logger, rkr, podName, controllerName()));
        getWorkerClientFactory().getWorkerForNamespace(rkr.getSpec().namespace()).invade(podName);
    }

}
