package org.apache.coyote.http11.response.body;

public class ResponseBody {

    private final String bodyMessage;

    public ResponseBody(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public int measureContentLength() {
        return bodyMessage.getBytes().length;
    }

    public String bodyMessage() {
        return bodyMessage;
    }
}
