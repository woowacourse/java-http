package org.apache.coyote.response;

public class ResponseBody {

    private String body;

    public ResponseBody() {
        this.body = "";
    }

    public ResponseBody(String body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLength() {
        return String.valueOf(body.getBytes().length);
    }

    public String getBody() {
        return body;
    }
}
