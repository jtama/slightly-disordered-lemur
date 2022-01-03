package com.onepoint.sdl.ri;

import com.onepoint.sdl.r.RandomController;
import com.onepoint.sdl.r.RandomRequestStatus;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.javaoperatorsdk.operator.api.Controller;
import okhttp3.Response;
import org.jboss.logging.Logger;

@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class RandomInvasionController extends RandomController<RandomInvasionRequest> {


    public RandomInvasionController(Logger logger, KubernetesClient client, Config config) {
        super(logger, client, config);
    }


    @Override
    protected RandomRequestStatus process(RandomInvasionRequest rkr, String podName) {
        getClient().pods().inNamespace(rkr.getSpec().namespace()).withName(podName)
                .writingOutput(System.out)
                .usingListener(new LogListener(getLogger()))
                .exec("sh", "-c", "echo \\\"%s\\\" > /tmp/invasion.txt".formatted(Invasion.BENDER.toString()).replace("\"", "\\\"").replace("'", "\\'"));
        return RandomRequestStatus.from(RandomRequestStatus.State.DONE, ("Slightly disordered lemure invaded '%s' 💀.").formatted(podName));
    }

    private class LogListener implements ExecListener {
        private Logger logger;

        public LogListener(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void onOpen(Response response) {
            logger.info("Beginning invasion:");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            logger.error("Damn, invasion failed", t);
        }

        @Override
        public void onClose(int code, String reason) {
            logger.info("Invasion successfull");
        }
    }
}
