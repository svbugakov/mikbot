package org.bot.ai;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.SimpleQuestion;
import org.bot.rag.Embedding;
import org.bot.rag.SimpleVectorDB;

import java.util.List;

public class RagModelClient {
    private final SimpleVectorDB vectorDB;

    public RagModelClient(
            SimpleVectorDB vectorDB
    ) {
        this.vectorDB = vectorDB;
    }


    public Question getResponse(Question question) {
        if (question.getQuestionGoal() != QuestionGoal.TEXT) {
            return question;
        }

        // 1. Поиск релевантных документов
        float[] questionEmbedding = Embedding.dl4JEmbedding(question.getMessage());
        List<String> relevantDocs = vectorDB.findSimilar(questionEmbedding, 3);

        // 2. Построение контекста
        String context = buildContext(relevantDocs);
        if (context.isEmpty()) {
            return question;
        }

        // 3. Формирование промпта с контекстом
        String prompt = buildPrompt(question.getMessage(), context);
        return new SimpleQuestion(prompt, QuestionGoal.TEXT);
    }

    private String buildContext(List<String> documents) {
        if (documents.isEmpty()) {
            return StringUtils.EMPTY;
        }

        StringBuilder context = new StringBuilder();
        context.append("Контекст для ответа:\n\n");

        for (int i = 0; i < documents.size(); i++) {
            context.append("Источник ").append(i + 1).append(":\n");
            context.append(documents.get(i)).append("\n\n");
        }

        return context.toString();
    }

    private String buildPrompt(String question, String context) {
        return String.format(
                "%s\n\n" +
                        "На основе приведённого контекста ответь на вопрос. " +
                        "Если в контексте нет информации для ответа, ответь используя свои знания.\n\n" +
                        "В ответе не нужно писать ничего про контекст, пользователю не интересно знать отуда информация\n\n" +
                        "Вопрос: %s\n\n" +
                        "Ответ:",
                context, question
        );
    }


}
