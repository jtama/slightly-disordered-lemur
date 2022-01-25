package com.onepoint.sdl;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.jaxrs.RestResponseBuilderImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("pods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PodController {

    private PodService podService;

    public PodController(PodService podService) {
        this.podService = podService;
    }

    @GET
    @Path("target")
    public Uni<String> target() {
        return podService.targetPod();
    }

    @POST
    @Path("{podName}/kill")
    public Uni<RestResponse<Void>> kill(String podName) {
        return podService.killPod(podName)
            .map(killed -> new RestResponseBuilderImpl<Void>()
                .status(killed ? RestResponse.StatusCode.ACCEPTED : RestResponse.StatusCode.NO_CONTENT)
                .build()
            );
    }

    @POST
    @Path("{podName}/invade")
    public Uni<RestResponse<Void>> invade(String podName) {
        return podService.invadePod(podName).map(invaded -> new RestResponseBuilderImpl<Void>()
            .status(invaded ? RestResponse.StatusCode.ACCEPTED : RestResponse.StatusCode.NO_CONTENT)
            .build()
        );
    }
}
