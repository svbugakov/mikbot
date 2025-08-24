package org.bot.ai.function;



import org.bot.ai.function.giga.GigaWeatherFunction;
import org.bot.ai.function.meteosource.ApiOpenMeteo;
import org.bot.ai.function.openai.OpenAIWeatherFunction;

import java.util.ArrayList;
import java.util.List;

public class AIFunctionManager {
    private final List<AIFunction> aiFunctions = new ArrayList<>();

    public AIFunctionManager() {
        ApiOpenMeteo apiOpenMeteo = new ApiOpenMeteo();
        aiFunctions.add(new OpenAIWeatherFunction(apiOpenMeteo));
        aiFunctions.add(new GigaWeatherFunction(apiOpenMeteo));
    }

    public List<AIFunction> getAiFunctions() {
        return aiFunctions;
    }
}
