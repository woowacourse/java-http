package org.apache.coyote.http11.message.request.body;

public class RequestBody {

    private final String value;

    public RequestBody(final String value) {
        this.value = value;
    }

    public static RequestBody ofNull() {
        return new RequestBody(null);
    }

    public String getValue() {
        return value;
    }
}
