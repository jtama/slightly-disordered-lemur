package com.onepoint.sdl.worker;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WorkerClient {

    @GET
    @Path("/pods/target")
    String target();

    @POST
    @Path("/pods/{podName}/kill")
    void kill(@PathParam("podName") String podName);

    @POST
    @Path("/pods/{podName}/invade")
    void invade(@PathParam("podName") String podName);
}
