package org.apache.catalina.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseContent {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CHARSET = "charset=utf-8";

    private final HttpStatus httpStatus;
    private final String body;
    private final Map<String, String> headers = new HashMap<>();

    public ResponseContent(HttpStatus httpStatus, String contentType, String body) {
        this.httpStatus = httpStatus;
        headers.put("Content-Type", contentType + "; " + DEFAULT_CHARSET);
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String responseToString() {
        StringBuilder response = new StringBuilder();

        response.append(HTTP_VERSION + " ")
                .append(httpStatus.getValue())
                .append(" ")
                .append(httpStatus.getReasonPhrase()).append(" ");
        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append("\r\n"));
        response.append("\r\n").append(body);

        return response.toString();
    }
}
