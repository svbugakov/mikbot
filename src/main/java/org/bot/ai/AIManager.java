package org.bot.ai;


import org.bot.PwdKeeper;
import org.bot.ResponseAI;
import org.bot.ai.function.AIFunctionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AIManager {
    private static final Logger logger = LoggerFactory.getLogger(AIManager.class);

    private final List<AbstractAI> aiAgents = new ArrayList<>();

    public AIManager(PwdKeeper pwdKeeper) {
        AIFunctionManager aiFunctionManager = new AIFunctionManager();
        aiAgents.add(new Gpt4oMiniModelClient(pwdKeeper.getPassword("gpt4o"), aiFunctionManager));
        aiAgents.add(new Gpt4oMiniWebClient(pwdKeeper.getPassword("gpt4o")));
        aiAgents.add(new DeepSeekWebClient(pwdKeeper.getPassword("deepseek")));
    }

    public String getResponse(String question) {
        String response = "Собачка отдыхает, потом спросите.";
        for (AbstractAI agent : aiAgents) {
            ResponseAI responseAI = agent.getResponse(question);
            if (responseAI.getCode() < 200 || responseAI.getCode() >= 300) {
                logger.warn("AI {} failed", agent.getName());
            } else {
                response = responseAI.getResponse();
                logger.info("AI {} successes", agent.getName());
                break;
            }
        }
        return response;
    }
}
