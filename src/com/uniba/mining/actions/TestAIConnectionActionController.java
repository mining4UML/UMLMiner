package com.uniba.mining.actions;

import javax.swing.JOptionPane;

import com.uniba.mining.llm.ApiRequest;
import com.uniba.mining.llm.ApiResponse;
import com.uniba.mining.llm.LLMConfig;
import com.uniba.mining.llm.LLMProvider;
import com.uniba.mining.llm.LLMProviderFactory;
import com.uniba.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class TestAIConnectionActionController implements VPActionController {

    @Override
    public void performAction(VPAction action) {

        try {            
            String providerName = LLMConfig.getProvider();

            LLMProvider provider =
                    LLMProviderFactory.createProvider();

            ApiRequest request =
                    new ApiRequest(
                            "test-session",
                            "test-project",
                            "test-diagram",
                            "test-query",
                            "Connection test.",
                            null,
                            "",
                            "",
                            "",
                            System.getProperty("user.name"),
                            "Reply only with OK."
                    );

            ApiResponse response =
                    provider.sendRequest(request);

            JOptionPane.showMessageDialog(
                    null,
                    "Connection successful.\n\n"
                    + "Selected provider: " + providerName + "\n\n"
                    + "Provider response:\n"
                    + response.getAnswer(),
                    "UML Miner - AI Connection",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
        	String providerName = providerLabel(LLMConfig.getProvider());
        	JOptionPane.showMessageDialog(
        	        null,
        	        "Connection failed.\n\n"
        	        + "Selected provider: " + providerName + "\n\n"
        	        + "Error:\n"
        	        + e.getMessage(),
        	        "UML Miner - AI Connection",
        	        JOptionPane.ERROR_MESSAGE
        	);
        }
    }
    
    private String providerLabel(String provider) {
        if ("remote".equalsIgnoreCase(provider)) {
            return "Remote RAG + LLM";
        }

        if ("local".equalsIgnoreCase(provider)) {
            return "Local LLM";
        }

        return provider;
    }

    @Override
    public void update(VPAction action) {
        action.setEnabled(true);
    }
}