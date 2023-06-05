package com.uniba.mining.tasks;

import task.discovery.DiscoveryTaskDeclare;
import task.discovery.DiscoveryTaskResult;

public class DeclareDiscoveryTask extends DiscoveryTaskDeclare implements DiscoveryTask {
    @Override
    public DiscoveryTaskResult call() {
        try {
            return super.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
