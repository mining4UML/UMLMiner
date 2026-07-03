package com.uniba.mining.actions;

import javax.swing.JOptionPane;

import com.uniba.mining.llm.LLMConfig;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class UseRemoteRAGActionController implements VPActionController {

    @Override
    public void performAction(VPAction action) {
        LLMConfig.setProvider("remote");

        JOptionPane.showMessageDialog(
                null,
                "AI provider set to Remote RAG + LLM.",
                "UML Miner - AI Configuration",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void update(VPAction action) {
        action.setEnabled(true);
    }
}