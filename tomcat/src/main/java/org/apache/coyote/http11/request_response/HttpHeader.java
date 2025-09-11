package org.apache.coyote.http11.request_response;

public class HttpHeader {

    private final String name;
    private final String value;

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean nameEquals(String other) {
        return name.toLowerCase().equals(other.toLowerCase());
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
