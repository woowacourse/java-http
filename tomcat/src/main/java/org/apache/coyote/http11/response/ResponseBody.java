package org.apache.coyote.http11.response;

public class ResponseBody implements Response {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public static ResponseBody empty() {
        return new ResponseBody("");
    }

    @Override
    public String getAsString() {
        return body;
    }
}
