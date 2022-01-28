package com.onepoint.sdl.ri;

import com.onepoint.sdl.r.RandomRequest;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("sld.onepoint.com")
@Version("v1alpha1")
@ShortNames("rir")
public class RandomInvasionRequest extends RandomRequest {

    @Override
    public String getDoneMessage(String podName) {
        return "Pod %s has been invaded. \uD83C\uDFAF".formatted(podName);
    }
}
