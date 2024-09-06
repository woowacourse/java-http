package org.apache.coyote.http11.domain.body;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
