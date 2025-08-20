package org.bot.ai.function;



import org.bot.ai.function.openai.OpenAIFunctionWeather;

import java.util.ArrayList;
import java.util.List;

public class AIFunctionManager {
    private final List<AIFunction> aiFunctions = new ArrayList<>();

    public AIFunctionManager() {
        aiFunctions.add(new OpenAIFunctionWeather());
    }

    public List<AIFunction> getAiFunctions() {
        return aiFunctions;
    }
}
