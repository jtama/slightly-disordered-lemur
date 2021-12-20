package com.onepoint.yap.ri;

import com.onepoint.yap.r.RandomRequest;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("yap.onepoint.com")
@Version("v1alpha1")
@ShortNames("rir")
public class RandomInvasionRequest extends RandomRequest{


}
