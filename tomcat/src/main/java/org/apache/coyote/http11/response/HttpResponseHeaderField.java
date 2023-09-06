package org.apache.coyote.http11.response;

public class HttpResponseHeaderField {

    private final String key;
    private final String value;

    public HttpResponseHeaderField(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
