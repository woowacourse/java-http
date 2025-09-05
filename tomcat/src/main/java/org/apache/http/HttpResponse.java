package org.apache.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.RequestProcessingException;

public class HttpResponse {

    private HttpVersion httpVersion;
    private StatusCode statusCode;
    private Map<String, String> header = new HashMap<>();
    private String body;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getMessage() {
        validateCanMakeMessage();
        if (body == null || body.isEmpty()) {
            return String.join("\r\n", makeStartLine(), makeHeaderLines(), "", body);
        }
        return String.join("\r\n", makeStartLine(), makeHeaderLines(), "", body);
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    private String makeStartLine() {
        return String.join(" ",
                httpVersion.getValue(),
                String.valueOf(statusCode.getCode()),
                statusCode.getMessage(),
                "");
    }

    private String makeHeaderLines() {
        List<String> headerLines = new ArrayList<>();
        List<String> keys = header.keySet().stream().toList();
        for (String key : keys) {
            String value = header.get(key);
            headerLines.add(key + ": " + value + " ");
        }
        headerLines.add("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ");
        return String.join("\r\n", headerLines);
    }

    private void validateCanMakeMessage() {
        if (httpVersion == null || statusCode == null) {
            throw new RequestProcessingException("주요 응답 필드가 비어있어 응답 메세지를 생성할 수 없습니다.");
        }
    }
}
