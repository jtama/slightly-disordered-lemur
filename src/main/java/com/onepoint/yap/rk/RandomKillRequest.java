package com.onepoint.yap.rk;

import com.onepoint.yap.r.RandomRequest;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("yap.onepoint.com")
@Version("v1alpha1")
@ShortNames("rkr")
public class RandomKillRequest extends RandomRequest {


}
