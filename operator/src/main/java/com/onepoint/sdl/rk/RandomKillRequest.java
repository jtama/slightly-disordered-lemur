package com.onepoint.sdl.rk;

import com.onepoint.sdl.r.RandomRequest;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("sld.onepoint.com")
@Version("v1alpha1")
@ShortNames("rkr")
public class RandomKillRequest extends RandomRequest {


    @Override
    public String getDoneMessage(String podName) {
        return "Pod %s has been billed. \uD83D\uDC80".formatted(podName);
    }
}
