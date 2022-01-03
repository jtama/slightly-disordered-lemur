package com.onepoint.sdl;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("kill")
public class KillController {

    private TargetingService targetingService;

    public KillController(TargetingService targetingService) {
        this.targetingService = targetingService;
    }

    @POST
    public Uni<String> target() {
        return targetingService.targetPod();
    }
}
