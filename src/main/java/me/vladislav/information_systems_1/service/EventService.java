package me.vladislav.information_systems_1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EventService {

    @Inject
    private EventBroadcaster broadcaster;

    public void sendEvent(String entity, String action, Object data) {
        String eventName = entity + "_" + action;
        broadcaster.broadcast(eventName, data);
    }

}
