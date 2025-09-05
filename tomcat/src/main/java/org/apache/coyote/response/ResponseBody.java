package org.apache.coyote.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
