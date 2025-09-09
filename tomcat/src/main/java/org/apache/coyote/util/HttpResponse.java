package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String httpVersion;

    private int statusCode;

    private String statusMessage;

    private Map<String, String> responseHeader = new HashMap<>();

    private String responseBody;

    public void putHeader(String key, String value) {
        if (responseHeader.get(key) != null) {
            throw new IllegalArgumentException("이미 존재하는 헤더입니다.");
        }
        responseHeader.put(key, value);
    }

    public void update(final String httpVersion, final int statusCode, final String statusMessage, final String responseBody) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        response.append(httpVersion + " " + statusCode + " " + statusMessage).append(" ").append("\r\n");

        for (Map.Entry<String, String> header : responseHeader.entrySet()) {
            response.append(header.getKey() + ": " + header.getValue()).append(" ").append("\r\n");
        }

        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }
}
