package org.apache.catalina.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private static final String DEFAULT_CHARSET = "charset=utf-8";

    private final StatusLine statusLine;
    private final String body;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(StatusLine statusLine, String contentType, String body) {
        this.statusLine = statusLine;
        headers.put("Content-Type", contentType + ";" + DEFAULT_CHARSET);
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addLocation(String url) {
        headers.put("Location", "http://localhost:8080" + url);
    }

    public String responseToString() {
        StringBuilder response = new StringBuilder();

        response.append(statusLine.getVersionOfProtocol()).append(" ")
                .append(statusLine.getHttpStatus()).append(" \r\n");
        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append(" \r\n"));
        response.append("\r\n").append(body);

        return response.toString();
    }
}
