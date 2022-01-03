package com.onepoint.sdl;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "sld")
public interface Configuration {

    String namespace();
}
