package org.apache.coyote.http11.httpResponse;

public class ResponseBody {

    private String body;

    private ResponseBody(final String body) {
        this.body = body;
    }

    public static ResponseBody empty() {
        return new ResponseBody("");
    }

    public ResponseBody build(final String body) {
        this.body = body;
        return this;
    }

    public String getBody() {
        return this.body;
    }
}
