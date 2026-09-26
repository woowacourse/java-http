package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final String httpVersion;
    private int statusCode;
    private String statusMessage;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse(String httpVersion, int statusCode, String statusMessage, String body) {
        validateHttpVersion(httpVersion);
        validateStatusCode(statusCode);
        validateStatusMessage(statusMessage);
        validateBody(body);
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
    }


    private void validateHttpVersion(String httpVersion) {
        if (httpVersion == null) {
            throw new IllegalArgumentException("HTTP Version은 null일 수 없습니다.");
        }
        if (httpVersion.isBlank()) {
            throw new IllegalArgumentException("HTTP Version은 공백일 수 없습니다.");
        }
    }

    private void validateStatusCode(int statusCode) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("HTTP 상태 코드는 100 이상 599 이하여야 합니다.");
        }
    }

    private void validateStatusMessage(String statusMessage) {
        if (statusMessage == null) {
            throw new IllegalArgumentException("HTTP 상태 메시지는 null일 수 없습니다.");
        }
        if (statusMessage.isBlank()) {
            throw new IllegalArgumentException("HTTP 상태 메시지는 공백일 수 없습니다.");
        }
    }

    private void validateBody(String body) {
        if (body == null) {
            throw new IllegalArgumentException("HTTP 응답 Body는 null일 수 없습니다.");
        }
    }

    public HttpResponse addHeader(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("HTTP 응답 헤더 이름은 null일 수 없습니다.");
        }
        if (value == null) {
            throw new IllegalArgumentException("HTTP 응답 헤더 값은 null일 수 없습니다.");
        }
        headers.put(name, value);
        return this;
    }

    public String toHttpMessage() {
        StringBuilder response = new StringBuilder();

        response.append(httpVersion)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        headers.forEach((name, value) ->
                response.append(name)
                        .append(": ")
                        .append(value)
                        .append("\r\n"));

        response.append("\r\n");
        response.append(body);

        return response.toString();
    }

    public void setStatus(int statusCode, String statusMessage) {
        validateStatusCode(statusCode);
        validateStatusMessage(statusMessage);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setBody(String body) {
        validateBody(body);
        this.body = body;
    }
}
