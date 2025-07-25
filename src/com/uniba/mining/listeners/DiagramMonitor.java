package com.uniba.mining.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;

public class DiagramMonitor {

    private IDiagramUIModel currentDiagram = null;
    private Timer monitoringTimer;

    public void start() {
        monitoringTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IDiagramUIModel active = ApplicationManager.instance()
                        .getDiagramManager()
                        .getActiveDiagram();

                if (active != null && !active.equals(currentDiagram)) {
                    currentDiagram = active;
                    FeedbackHandler.getInstance().showFeedbackPanel(active);
                } else if (active == null && currentDiagram != null) {
                    FeedbackHandler.getInstance().hideFeedbackPanelIfShown(currentDiagram);
                    currentDiagram = null;
                }
            }
        });

        monitoringTimer.start();
    }

    public void stop() {
        if (monitoringTimer != null) {
            monitoringTimer.stop();
        }
    }

    public IDiagramUIModel getCurrentDiagram() {
        return currentDiagram;
    }
}
