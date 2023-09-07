package org.apache.coyote.http11;

public class RequestBody {
    private final String body;

    public RequestBody(final String body) {
        this.body = body;
    }

    public String[] split(final String regex) {
        return body.split(regex);
    }
}
