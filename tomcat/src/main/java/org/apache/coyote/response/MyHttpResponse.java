package org.apache.coyote.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.cookie.HttpCookie;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class MyHttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private String statusLine;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public void setStatusCode(StatusCode statusCode) {
        statusLine = String.join(" ",
                HTTP_VERSION,
                String.valueOf(statusCode.getStatusCode()),
                statusCode.getReasonPhrase()
        );
    }

    public void addCookie(HttpCookie httpCookie) {
        Map<String, String> cookies = httpCookie.cookies();
        for (Entry<String, String> entry : cookies.entrySet()) {
            addHeader(
                    "Set-Cookie",
                    String.join("=", entry.getKey(), entry.getValue())
            );
        }
    }

    public void addHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        headers.put(name, value);
    }

    public void sendRedirect(String redirectLocation) {
        Objects.requireNonNull(redirectLocation);
        headers.put(
                "Location",
                "http://localhost:8080/" + redirectLocation
        );
    }

    public void setContentType(ContentType contentType) {
        Objects.requireNonNull(contentType);
        headers.put(
                "Content-Type",
                contentType.toString()
        );
    }

    public void writeBody(String body) {
        Objects.requireNonNull(body);
        this.body = body;
        setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
    }

    private void setContentLength(int contentLength) {
        headers.put(
                "Content-Length",
                String.valueOf(contentLength)
        );
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(statusLine).append(" \r\n");

        if (body == null || body.isEmpty()) {
            setContentLength(0);
        }
        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append(" \r\n");
        }

        if (body != null) {
            sb.append("\r\n");
            sb.append(body);
        }
        return sb.toString();
    }
}
