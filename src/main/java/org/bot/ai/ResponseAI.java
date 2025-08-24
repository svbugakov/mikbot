package org.bot.ai;

public class ResponseAI {
    private String response;
    private StatusResponse status;
    private byte[] responseBytes;
    private String redirectAiName;
    private QuestionGoal questionGoal;

    public ResponseAI(String response, StatusResponse status) {
        this.response = response;
        this.status = status;
    }

    public ResponseAI(byte[] responseBytes, StatusResponse status) {
        this.status = status;
        this.responseBytes = responseBytes;
    }

    public ResponseAI(String response, StatusResponse status, byte[] responseBytes, String redirectAiName, QuestionGoal questionGoal) {
        this.response = response;
        this.status = status;
        this.responseBytes = responseBytes;
        this.redirectAiName = redirectAiName;
        this.questionGoal = questionGoal;
    }

    public String getRedirectAiName() {
        return redirectAiName;
    }

    public void setRedirectAiName(String redirectAiName) {
        this.redirectAiName = redirectAiName;
    }

    public QuestionGoal getQuestionGoal() {
        return questionGoal;
    }

    public void setQuestionGoal(QuestionGoal questionGoal) {
        this.questionGoal = questionGoal;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setCode(StatusResponse status) {
        this.status = status;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }
}
