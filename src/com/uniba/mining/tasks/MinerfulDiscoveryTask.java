package com.uniba.mining.tasks;

import task.discovery.DiscoveryTaskMinerful;
import task.discovery.DiscoveryTaskResult;

public class MinerfulDiscoveryTask extends DiscoveryTaskMinerful implements DiscoveryTask {
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
