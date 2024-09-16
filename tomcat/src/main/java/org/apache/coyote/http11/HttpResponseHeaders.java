package org.apache.coyote.http11;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponseHeaders {

    private final Map<HttpHeaderField, String> headers;

    public HttpResponseHeaders() {
        this.headers = new EnumMap<>(HttpHeaderField.class);
    }

    public String findField(HttpHeaderField key) {
        return this.headers.get(key);
    }

    public void setField(HttpHeaderField key, String value) {
        this.headers.put(key, value);
    }

    public void getHeadersToString(StringBuilder stringBuilder) {
        for (Entry<HttpHeaderField, String> headers : this.headers.entrySet()) {
            stringBuilder.append(headers.getKey().getHeaderField() + ": " + headers.getValue() + " ").append("\r\n");
        }
    }
}
