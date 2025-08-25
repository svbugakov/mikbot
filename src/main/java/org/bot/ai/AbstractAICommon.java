package org.bot.ai;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAICommon<M> implements AbstractAI<M> {
    public void logicRecreationAllMessages() {
        if (getAllMessage().size() > MAX_MESSAGES) {
            synchronized (this) {
                if (getAllMessage().size() > MAX_MESSAGES) {
                    logger.info("recreation messages in chat {}", getName());
                    List<M> newMessages = new ArrayList<>(getAllMessage().subList(
                            Math.max(0, getAllMessage().size() - 2),
                            getAllMessage().size()
                    ));
                    setAllMessage(newMessages);
                }
            }
        }
    }

    public ResponseAI tryNeedRedirect(Question question) {
        final String messageUp = question.getMessage().toUpperCase();
        if ((messageUp.contains("ПОГОД") || messageUp.contains("ПРОГНОЗ")) &&
                !getName().equals(Gpt4oMiniModelClient.NAME_AI)) {
            return new ResponseAI(question.getMessage(), StatusResponse.SUCCESS,
                    null, Gpt4oMiniModelClient.NAME_AI, QuestionGoal.TEXT);
        } else if (messageUp.contains("РИСУ") || messageUp.contains("КАРТИН")) {
            return new ResponseAI(question.getMessage(), StatusResponse.SUCCESS,
                    null, "giga", QuestionGoal.PICTURE);
        }
        return new ResponseAI(StringUtils.EMPTY, StatusResponse.SUCCESS);
    }

    public List<M> getAllMessage() {
        return new ArrayList<>();
    }

    public void setAllMessage(List<M> messNew) {
    }
}
