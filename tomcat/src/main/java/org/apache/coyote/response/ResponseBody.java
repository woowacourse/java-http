package org.apache.coyote.response;

public class ResponseBody {

    private String body;

    public ResponseBody() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
