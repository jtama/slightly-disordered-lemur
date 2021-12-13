package com.onepoint.yap;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("yap.onepoint.com")
@Version("v1alpha1")
@ShortNames("rkr")
public class RandomKillRequest extends CustomResource<RandomKillRequestSpec, RandomKillRequestStatus> implements Namespaced {


}
