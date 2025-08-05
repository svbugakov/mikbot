package org.bot.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepSeekWebClient extends AbstractAI {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekWebClient.class);
    private String apiKey;

    public DeepSeekWebClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getName() {
        return "deepseek-openroute";
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getModel() {
        return "deepseek/deepseek-chat-v3-0324:free";
    }

    @Override
    public String getRequestModel() {
        return """
                {
                    "model": "%s",
                    "messages": [
                        {"role": "user", "content": "%s"}
                    ]
                }
                """;
    }

    @Override
    public String getUri() {
        return "https://openrouter.ai/api/v1/chat/completions";
    }
}