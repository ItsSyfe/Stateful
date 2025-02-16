package me.syfe.stateful.util;

public class PortalProcessor implements Runnable {
    private final EntityListeners entityListeners;

    public PortalProcessor(EntityListeners entityListeners) {
        this.entityListeners = entityListeners;
    }

    public void run() {
        this.entityListeners.clearPortalEntities();
    }
}
