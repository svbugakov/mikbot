package org.bot.ai;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public interface AbstractAI<M> {
    int MAX_MESSAGES = 10;


    Logger logger = LoggerFactory.getLogger(AbstractAI.class);

    String getName();

    String getApiKey();

    String getModel();

    String getRequestModel();

    String getUri();

    ResponseAI getResponse(Question question);

    List<M> getAllMessage();

    void setAllMessage(List<M> messNew);

    void logicRecreationAllMessages();

    ResponseAI tryNeedRedirect(Question question);
}
