package me.vladislav.information_systems_1.cache.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@Getter
@ApplicationScoped
public class CacheLoggingConfig {

    private volatile boolean enabled = true;

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}

