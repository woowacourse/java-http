package org.apache.coyote.http11.support;

import static org.apache.coyote.http11.support.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.support.HttpHeader.CONTENT_TYPE;

import java.util.Map;
import java.util.Objects;

public class HttpHeaders {

    private final Map<HttpHeader, String> values;

    public HttpHeaders(final Map<HttpHeader, String> values) {
        this.values = values;
    }

    public String getContentLength() {
        return values.getOrDefault(CONTENT_LENGTH, "0");
    }

    public void put(final HttpHeader httpHeader, final String value) {
        values.put(httpHeader, value);
    }

    public Map<HttpHeader, String> getValues() {
        return values;
    }

    public boolean existsContentType() {
        return values.containsKey(CONTENT_TYPE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpHeaders)) return false;
        final HttpHeaders that = (HttpHeaders) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "values=" + values +
                '}';
    }
}
