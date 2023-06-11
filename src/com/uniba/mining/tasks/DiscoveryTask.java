package com.uniba.mining.tasks;

import java.io.File;

import task.discovery.DiscoveryTaskResult;

public interface DiscoveryTask {
    void setLogFile(File logFile);

    DiscoveryTaskResult call();
}
