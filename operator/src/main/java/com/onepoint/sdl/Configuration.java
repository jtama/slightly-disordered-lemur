package com.onepoint.sdl;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

@ConfigMapping(prefix = "sld")
public interface Configuration {

    String[] namespaces();

    @WithDefault("quay.io/jtama")
    String privateRegistry();

    Optional<String> secret();
}
