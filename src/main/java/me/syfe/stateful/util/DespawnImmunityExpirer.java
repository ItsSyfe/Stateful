package me.syfe.stateful.util;

public class DespawnImmunityExpirer implements Runnable {
    private final DespawnImmunityManager manager;

    public DespawnImmunityExpirer(DespawnImmunityManager manager) {
        this.manager = manager;
    }

    public void run() {
        this.manager.processPendingImmunityRemovalTasks();
    }
}