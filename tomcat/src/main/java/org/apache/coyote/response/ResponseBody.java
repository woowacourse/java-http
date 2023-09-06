package org.apache.coyote.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public String getLength() {
        return String.valueOf(body.getBytes().length);
    }

    public String getBody() {
        return body;
    }
}
