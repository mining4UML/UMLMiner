package com.uniba.mining.tasks;

import task.conformance.ConformanceTaskReplayer;
import task.conformance.ConformanceTaskResult;

public class ConformanceReplayerTask extends ConformanceTaskReplayer implements ConformanceTask {
    @Override
    public ConformanceTaskResult call() {
        try {
            return super.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
