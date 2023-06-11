package com.uniba.mining.tasks;

import task.conformance.ConformanceTaskAnalyzer;
import task.conformance.ConformanceTaskResult;

public class ConformanceAnalyzerTask extends ConformanceTaskAnalyzer implements ConformanceTask {
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
