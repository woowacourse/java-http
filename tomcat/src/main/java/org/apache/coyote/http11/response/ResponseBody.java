package org.apache.coyote.http11.response;

public class ResponseBody {

    private String bodyMessage;

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
