package com.onepoint.sdl;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Optional;

@ConfigMapping(prefix = "sld")
public interface Configuration {

    String[] namespaces();

    @WithDefault("quay.io/jtama")
    String privateRegistry();

    @WithName("worker-host")
    @WithDefault("sld-worker-svc.%s")
    String workerHost();

    Optional<String> secret();
}
