package com.uniba.mining.llm;

import java.io.IOException;

public interface LLMProvider {
    ApiResponse sendRequest(ApiRequest request) throws IOException;
}