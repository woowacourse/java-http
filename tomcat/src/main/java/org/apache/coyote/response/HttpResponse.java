package org.apache.coyote.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    public static final String CRLF = "\r\n";

    private StatusLine statusLine;
    private final Map<String, String> headers;
    private String body;

    public static HttpResponse create() {
        Map<String, String> headers = new HashMap<>();
        StatusCode statusCode = StatusCode.OK;

        return new HttpResponse(StatusLine.build(statusCode), headers, "");
    }

    private HttpResponse(StatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        response.append(statusLine);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        response.append(CONTENT_LENGTH).append(": ")
                .append(body.getBytes(StandardCharsets.UTF_8).length)
                .append(" ")
                .append(CRLF);
        response.append(CRLF);
        response.append(body);

        return response.toString();
    }

    public void sendRedirect(String location) {
        statusLine = StatusLine.build(StatusCode.FOUND);
        headers.put(LOCATION, location);
    }

    public void setStatus(StatusCode statusCode) {
        statusLine = StatusLine.build(statusCode);
    }

    public void setBody(String body) {
        this.body = body;
    }
}
