package com.onepoint.sdl.worker;

import com.onepoint.sdl.Configuration;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;

@ApplicationScoped
public class WorkerClientFactory {

    private Configuration config;

    public WorkerClientFactory(Configuration config) {
        this.config = config;
    }

    public WorkerClient getWorkerForNamespace(String namespace) {
        return RestClientBuilder.newBuilder()
            .baseUri(URI.create("http://%s".formatted(config.workerHost().formatted(namespace))))
            .build(WorkerClient.class);
    }

}
