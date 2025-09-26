package org.bot.ai;


import org.bot.PwdKeeper;
import org.bot.SslContextKeeper;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.bot.ai.function.AIFunctionManager;
import org.bot.rag.SimpleVectorDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AIManager {
    private static final Logger logger = LoggerFactory.getLogger(AIManager.class);

    private final List<AbstractAI> aiAgents = new ArrayList<>();
    final String ERROR_RESP = "Собачка отдыхает, потом спросите.";
    private final RagModelClient gigaRagModelClient;

    public AIManager(PwdKeeper pwdKeeper, SslContextKeeper sslContextKeeper) {
        AIFunctionManager aiFunctionManager = new AIFunctionManager();
        aiAgents.add(new DeepSeekWebClient(pwdKeeper.getPassword("deepseek")));
        aiAgents.add(new Gpt4oMiniModelClient(pwdKeeper.getPassword("gpt4o"), aiFunctionManager));
        aiAgents.add(new GigaModelClient(pwdKeeper.getPassword("giga"), aiFunctionManager));
        aiAgents.add(new SttWebAI("https://5.129.220.87:443/asr", sslContextKeeper));

        SimpleVectorDB rag = new SimpleVectorDB();
        gigaRagModelClient = new RagModelClient(rag);
        fillSimpleDB(rag);
    }

    public ResponseAI getResponse(Question question) {
        for (AbstractAI agent : aiAgents) {
            Question ragQuestion = gigaRagModelClient.getResponse(question);
            ResponseAI responseAI = agent.getResponse(ragQuestion);
            if (responseAI.getStatus() == StatusResponse.FAILED) {
                logger.warn("AI {} failed", agent.getName());
                continue;
            }
            if (responseAI.getRedirectAiName() == null) {
                logger.info("AI {} successes", agent.getName());
                return responseAI;
            }
            //redirect ai agent
            ResponseAI responseAIR = findAgentByName(responseAI.getRedirectAiName()).getResponse(question);
            if (responseAIR.getStatus() == StatusResponse.SUCCESS) {
                logger.warn("AI redirect {} success", agent.getName());
                return responseAIR;
            }
            logger.warn("AI redirect {} failed", agent.getName());
        }
        return new ResponseAI(ERROR_RESP, StatusResponse.FAILED);
    }

    private AbstractAI findAgentByName(final String name) {
        Optional<AbstractAI> agentOpt = aiAgents.stream().filter(agent -> agent.getName().equals(name))
                .findFirst();
        if (agentOpt.isEmpty()) {
            throw new RuntimeException("no found agent %s".formatted(name));
        }
        return agentOpt.get();
    }

    private void fillSimpleDB(SimpleVectorDB rag) {


    }
}
