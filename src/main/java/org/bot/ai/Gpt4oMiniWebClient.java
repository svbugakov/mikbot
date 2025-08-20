package org.bot.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gpt4oMiniWebClient extends AbstractGtp4oMini {

    private static final Logger logger = LoggerFactory.getLogger(Gpt4oMiniWebClient.class);

    public Gpt4oMiniWebClient(String apiKey) {
        super(apiKey);
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