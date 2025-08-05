package org.bot.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gpt4oMiniWebClient extends AbstractAI {

    private static final Logger logger = LoggerFactory.getLogger(Gpt4oMiniWebClient.class);
    private String apiKey;

    public Gpt4oMiniWebClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getName() {
        return "gpt-4o-mini";
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public String getModel() {
        return "gpt-4o-mini";
    }

    @Override
    public String getRequestModel() {
        return """
                {
                     "model": "%s",
                     "messages": [
                       {
                         "role": "system",
                         "content": "You are a helpful assistant."
                       },
                       {
                         "role": "user",
                         "content": "%s."
                       }
                     ],
                     "max_tokens": 150,
                     "temperature": 0.7
                   }
                """;
    }

    @Override
    public String getUri() {
        return "https://api.openai.com/v1/chat/completions";
    }
}