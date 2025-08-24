package org.bot.ai;

public class SimpleQuestion implements Question {
    private String message;
    private QuestionGoal questionGoal;

    public SimpleQuestion(String message, QuestionGoal questionGoal) {
        this.message = message;
        this.questionGoal = questionGoal;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public QuestionGoal getQuestionGoal() {
        return questionGoal;
    }
}
