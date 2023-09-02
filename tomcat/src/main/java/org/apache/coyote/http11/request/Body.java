package org.apache.coyote.http11.request;

public class Body {

    private final String body;

    public Body(String body) {
        this.body = body;
    }

    public String body() {
        return body;
    }
}
