package org.apache.coyote.http11.request;

public class RequestUrl {

    private final String value;

    public RequestUrl(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
