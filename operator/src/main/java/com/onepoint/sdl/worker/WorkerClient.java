package com.onepoint.sdl.worker;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public interface WorkerClient {

    @GET
    @Path("/pods/target")
    String target();

    @POST
    @Path("/pods/kill")
    void kill(String podName);

    @Path("/pods/invade")
    void invade(String podName);
}
