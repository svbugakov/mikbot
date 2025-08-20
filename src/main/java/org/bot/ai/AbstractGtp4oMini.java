package org.bot.ai;

public abstract class AbstractGtp4oMini extends AbstractAI {

    private String apiKey;

    public AbstractGtp4oMini(String apiKey) {
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
}
