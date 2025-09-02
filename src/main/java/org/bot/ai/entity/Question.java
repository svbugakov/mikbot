package org.bot.ai.entity;

public interface Question {
    String getMessage();
    QuestionGoal getQuestionGoal();
    byte[] getBytes();
}
