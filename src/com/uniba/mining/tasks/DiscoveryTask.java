package com.uniba.mining.tasks;

import java.io.File;

import task.discovery.DiscoveryTaskResult;
import task.discovery.mp_enhancer.MpEnhancer;

public interface DiscoveryTask {
    void setLogFile(File logFile);

    void setMpEnhancer(MpEnhancer mpEnhancer);

    DiscoveryTaskResult call();
}
