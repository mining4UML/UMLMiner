package com.uniba.mining.tasks;

import java.io.File;

import task.discovery.DiscoveryTaskResult;

public interface DiscoveryTask {
    public DiscoveryTaskResult call();

    public void setLogFile(File logFile);
}
