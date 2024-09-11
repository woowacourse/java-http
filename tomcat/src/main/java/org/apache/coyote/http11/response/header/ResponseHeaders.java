package org.apache.coyote.http11.response.header;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;

public class ResponseHeaders {

    private final LinkedHashMap<HttpHeader, String> headers;

    public ResponseHeaders(LinkedHashMap<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addContentHeaders(ContentType contentType, String body) {
        headers.put(HttpHeader.CONTENT_TYPE, contentType.value());
        headers.put(HttpHeader.CONTENT_LENGTH, Integer.toString(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void addHeader(HttpHeader name, String value) {
        headers.put(name, value);
    }

    public String write() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<HttpHeader, String> entry : headers.entrySet()) {
            builder.append(entry.getKey().getName())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        return builder.toString();
    }
}
