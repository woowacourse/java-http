package org.apache.coyote.http11.request;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public String getValue() {
        return body;
    }
}
