package com.onepoint.yap.r;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;


public class RandomRequest extends CustomResource<RandomRequestSpec, RandomRequestStatus> implements Namespaced {


}
