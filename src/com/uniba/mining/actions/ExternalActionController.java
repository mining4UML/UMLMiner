package com.uniba.mining.actions;

import java.io.IOException;

import com.uniba.mining.plugin.Config;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExternalActionController implements VPActionController {
    public static final String ACTION_NAME = "External";

    @Override
    public void performAction(VPAction vpAction) {
        System.out.println(ACTION_NAME);
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", Config.RUM_PATH);
        try {
            Process process = processBuilder.start();
            System.out.println(process.waitFor());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
