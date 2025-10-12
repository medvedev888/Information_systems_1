package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import jakarta.ws.rs.sse.OutboundSseEvent;

import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class EventBroadcaster {
    private volatile Sse sse;
    private volatile SseBroadcaster broadcaster;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public void init(Sse sse) {
        if (initialized.compareAndSet(false, true)) {
            this.sse = sse;
            this.broadcaster = sse.newBroadcaster();
        }
    }

    public void register(SseEventSink sink) {
        if (broadcaster == null) {
            throw new IllegalStateException("EventBroadcaster not initialized. Call init(Sse) first from controller.");
        }
        broadcaster.register(sink);
    }

    public void broadcast(String eventName, Object payload) {
        if (broadcaster == null || sse == null) {
            System.err.println("EventBroadcaster: not initialized yet. Dropping event: " + eventName);
            return;
        }

        OutboundSseEvent event = sse.newEventBuilder()
                .name(eventName)
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(payload)
                .build();

        broadcaster.broadcast(event);
    }

}
