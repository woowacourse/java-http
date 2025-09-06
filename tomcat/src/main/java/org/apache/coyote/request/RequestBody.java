package org.apache.coyote.request;

public class RequestBody {

    private final String body;

    public RequestBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
