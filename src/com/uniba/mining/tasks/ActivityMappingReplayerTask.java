package com.uniba.mining.tasks;

import task.conformance.ActivityMappingResult;
import task.conformance.ActivityMappingTask;

public class ActivityMappingReplayerTask extends ActivityMappingTask {
    @Override
    public ActivityMappingResult call() {
        try {
            return super.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
