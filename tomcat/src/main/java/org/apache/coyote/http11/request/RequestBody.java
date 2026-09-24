package org.apache.coyote.http11.request;

public class RequestBody {

    private final String requestBody;

    public RequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public String getValue() {
        return requestBody;
    }
}
