package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.general.ContentType;

public class HttpResponse {

    private final Map<String, String> headers;
    private final HttpStatus httpStatus;
    private final byte[] body;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.httpStatus = httpStatus;
        if (body == null) {
            this.body = new byte[0];
        } else {
            this.body = body.getBytes(StandardCharsets.UTF_8);
        }
        this.headers = new HashMap<>();
        this.headers.put("Content-Length", String.valueOf(this.body.length));
        this.headers.put("Content-Type", contentType.getValueWithUtf8Charset());
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String toString() {
        return String.join("\r\n",
            "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage(),
            buildHeaderMessage(),
            new String(body, StandardCharsets.UTF_8));
    }

    private String buildHeaderMessage() {
        StringBuilder result = new StringBuilder();
        for (Entry<String, String> header : headers.entrySet()) {
            result.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        return result.toString();
    }
}
