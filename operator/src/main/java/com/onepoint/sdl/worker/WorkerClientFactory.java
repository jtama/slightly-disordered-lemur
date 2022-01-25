package com.onepoint.sdl.worker;

import com.onepoint.sdl.Configuration;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;

@ApplicationScoped
public class WorkerClientFactory {

    private Configuration config;
    private Logger logger;

    public WorkerClientFactory(Configuration config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public WorkerClient getWorkerForNamespace(String namespace) {
        var url = "http://%s".formatted(config.workerHost().formatted(namespace));
        logger.infof("Generating client for %s worker with url: %s", namespace, url);
        return RestClientBuilder.newBuilder()
            .baseUri(URI.create(url))
            .build(WorkerClient.class);
    }

}
