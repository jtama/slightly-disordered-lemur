package com.onepoint.yap.rk;

import com.onepoint.yap.r.RandomController;
import com.onepoint.yap.r.RandomRequestStatus;
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
        if (!rkr.getSpec().targetOnly()) {
            getClient().pods().withName(podName).delete();
            return RandomRequestStatus.from(RandomRequestStatus.State.DONE, ("RandomKillRequest killed '%s' 💀.").formatted(podName));
        }
        return RandomRequestStatus.from(RandomRequestStatus.State.DONE, "RandomKillRequest target is '%s' 🎯".formatted(podName));
    }
}
