package me.vladislav.information_systems_1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import me.vladislav.information_systems_1.service.EventBroadcaster;

@Path("/events")
public class EventController {

    @Inject
    private EventBroadcaster broadcaster;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sink, @Context Sse sse) {
        broadcaster.init(sse);
        broadcaster.register(sink);
    }

}
