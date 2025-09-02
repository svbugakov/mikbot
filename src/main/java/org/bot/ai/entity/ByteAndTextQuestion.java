package org.bot.ai.entity;

public class ByteAndTextQuestion implements Question {
    private String message;
    private QuestionGoal questionGoal;
    private byte[] bytes;

    public ByteAndTextQuestion(String message, QuestionGoal questionGoal, byte[] bytes) {
        this.message = message;
        this.questionGoal = questionGoal;
        this.bytes = bytes;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public QuestionGoal getQuestionGoal() {
        return questionGoal;
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }
}
