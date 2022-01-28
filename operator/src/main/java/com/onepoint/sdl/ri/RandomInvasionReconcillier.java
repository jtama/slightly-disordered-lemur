package com.onepoint.sdl.ri;

import com.onepoint.sdl.r.RandomReconcillier;
import com.onepoint.sdl.worker.WorkerClientFactory;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import org.jboss.logging.Logger;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_CURRENT_NAMESPACE;

@ControllerConfiguration(namespaces = WATCH_CURRENT_NAMESPACE)
public class RandomInvasionReconcillier extends RandomReconcillier<RandomInvasionRequest> {


    public RandomInvasionReconcillier(Logger logger, KubernetesClient client, Config config, WorkerClientFactory workerClientFactory) {
        super(logger, client, config, workerClientFactory);
    }

    @Override
    protected String controllerName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void process(RandomInvasionRequest rkr, String podName) {
        client.pods().inNamespace(rkr.getSpec().namespace()).withName(podName).watch(new InvadePodWatcher(client, logger, rkr, podName, controllerName()));
        logger.debugf("Invoking worker.", this.toString());
        getWorkerClientFactory().getWorkerForNamespace(rkr.getSpec().namespace()).invade(podName);
    }

}
