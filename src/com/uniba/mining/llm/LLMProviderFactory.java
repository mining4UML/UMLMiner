package com.uniba.mining.llm;

public class LLMProviderFactory {

    public static LLMProvider createProvider() {

        String provider =
                LLMConfig.getProvider();

        if ("remote".equalsIgnoreCase(provider)) {
            return new RemoteServerProvider();
        }

        return new LocalOpenAIProvider();
    }

    private LLMProviderFactory() {
    }
}