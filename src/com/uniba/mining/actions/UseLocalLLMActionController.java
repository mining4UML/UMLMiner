package com.uniba.mining.actions;

import javax.swing.JOptionPane;

import com.uniba.mining.llm.LLMConfig;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class UseLocalLLMActionController implements VPActionController {

    @Override
    public void performAction(VPAction action) {
        LLMConfig.setProvider("local");

        JOptionPane.showMessageDialog(
                null,
                "AI provider set to Local LLM.",
                "UML Miner - AI Configuration",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void update(VPAction action) {
        action.setEnabled(true);
    }
}