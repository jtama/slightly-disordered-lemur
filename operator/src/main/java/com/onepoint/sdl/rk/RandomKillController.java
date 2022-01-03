package com.onepoint.sdl.rk;

import com.onepoint.sdl.r.RandomController;
import com.onepoint.sdl.r.RandomRequestStatus;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.Controller;
import org.jboss.logging.Logger;

@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class RandomKillController extends RandomController<RandomKillRequest> {

    public RandomKillController(Logger logger, KubernetesClient client, Config config) {
        super(logger, client, config);
    }

    protected RandomRequestStatus process(RandomKillRequest rkr, String podName) {
        getClient().pods().inNamespace(rkr.getSpec().namespace()).withName(podName).delete();
        return RandomRequestStatus.from(RandomRequestStatus.State.DONE, ("Slightly disordered lemure killed '%s' 💀.").formatted(podName));
    }
}
