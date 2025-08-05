package org.bot;

public class ResponseAI {
    private String response;
    private int code;

    public ResponseAI(String response, int code) {
        this.response = response;
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
