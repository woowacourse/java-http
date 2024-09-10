package org.apache.coyote.http11.httprequest;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public String getValue() {
        return body;
    }
}
