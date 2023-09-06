package org.apache.coyote.response;

public class ResponseBody {

    private final String body;

    ResponseBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
