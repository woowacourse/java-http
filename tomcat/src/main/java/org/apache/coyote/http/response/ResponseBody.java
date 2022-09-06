package org.apache.coyote.http.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(final String body) {
        this.body = body;
    }

    public int calculateLength() {
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }
}
