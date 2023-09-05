package org.apache.coyote.http11.request;

public class HttpRequestUri {

    private final String value;

    private HttpRequestUri(final String value) {
        this.value = value;
    }

    public static HttpRequestUri from(String uri) {
        return new HttpRequestUri(uri);
    }

    public boolean contains(String uri) {
        return value.contains(uri);
    }

    public boolean same(String uri) {
        return value.equals(uri);
    }

    public String getValue() {
        return value;
    }
}
