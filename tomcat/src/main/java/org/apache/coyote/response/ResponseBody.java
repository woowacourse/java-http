package org.apache.coyote.response;

public class ResponseBody {

    private String body;

    public ResponseBody(final String body) {
        this.body = body;
    }

    public ResponseBody() {}

    public String getBody() {
        return body;
    }

    public ResponseBody init(final String body) {
        this.body = body;
        return this;
    }
}