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
    private Map<String, String> headers = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private String body;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getMessage() {
        validateCanMakeMessage();
        String startAndHeader = String.join("\r\n", makeStartLine(), makeHeaderLines());
        if (body != null && !body.isEmpty()) {
            return String.join("\r\n", startAndHeader, "", body);
        }
        return startAndHeader;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String makeStartLine() {
        return String.format("%s %s %s ",
                httpVersion.getValue(),
                statusCode.getCode(),
                statusCode.getMessage());
    }

    private String makeHeaderLines() {
        List<String> headerLines = new ArrayList<>();
        addCustomHeaderLine(headerLines);
        addSetCookieHeaderLine(headerLines);
        addContentLengthHeaderLine(headerLines);
        return String.join("\r\n", headerLines);
    }

    private void addCustomHeaderLine(List<String> headerLines) {
        List<String> customHeaderKeys = headers.keySet().stream().toList();
        for (String key : customHeaderKeys) {
            String value = headers.get(key);
            headerLines.add(key + ": " + value + " ");
        }
    }

    private void addSetCookieHeaderLine(List<String> headerLines) {
        if (cookies.isEmpty()) {
            return;
        }
        List<String> cookieLines = cookies.stream().map(Cookie::makeCookieLine).toList();
        headerLines.add("Set-Cookie: " + String.join(" ", cookieLines));
    }

    private void addContentLengthHeaderLine(List<String> headerLines) {
        if (body == null) {
            return;
        }
        headerLines.add("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ");
    }

    private void validateCanMakeMessage() {
        if (httpVersion == null || statusCode == null) {
            throw new RequestProcessingException("주요 응답 필드가 비어있어 응답 메세지를 생성할 수 없습니다.");
        }
    }
}
