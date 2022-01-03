package com.onepoint.sdl.r;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;


public class RandomRequest extends CustomResource<RandomRequestSpec, RandomRequestStatus> implements Namespaced {


}
