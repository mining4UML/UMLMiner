package com.uniba.mining.llm;

import java.io.IOException;

public class RemoteServerProvider implements LLMProvider {

    @Override
    public ApiResponse sendRequest(ApiRequest request) throws IOException {
        RestClient client = new RestClient();
        return client.sendRequest(request);
    }
}