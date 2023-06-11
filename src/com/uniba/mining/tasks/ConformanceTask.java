package com.uniba.mining.tasks;

import java.io.File;

import task.conformance.ConformanceTaskResult;

public interface ConformanceTask {
    void setXmlModel(File xmlModel);

    void setLogFile(File logFile);

    ConformanceTaskResult call();
}
