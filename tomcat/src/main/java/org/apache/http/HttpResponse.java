package org.apache.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.RequestProcessingException;
import org.apache.exception.SocketWriteException;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.HttpVersion;
import org.apache.http.value.StatusCode;

public class HttpResponse {

    private HttpVersion httpVersion;
    private StatusCode statusCode;
    private Map<String, String> headers = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private String body;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void writeMessage(OutputStream outputStream) {
        String message = getMessage();
        try {
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new SocketWriteException("소켓에 데이터를 쓰는중 오류가 발생했습니다.");
        }
    }

    public boolean isProcessed() {
        return statusCode != null && !headers.isEmpty();
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

    public void setRedirection(String location) {
        setStatusCode(StatusCode.FOUND);
        setHeader(HttpHeader.LOCATION.getValue(), location);
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
        String setCookieHeader = String.format("%s: %s",
                HttpHeader.SET_COOKIE.getValue(),
                String.join(" ", cookieLines));
        headerLines.add(setCookieHeader);
    }

    private void addContentLengthHeaderLine(List<String> headerLines) {
        if (body == null) {
            return;
        }
        String contentLengthHeader = String.format("%s: %s",
                HttpHeader.CONTENT_LENGTH.getValue(),
                body.getBytes(StandardCharsets.UTF_8).length + " ");
        headerLines.add(contentLengthHeader);
    }

    private void validateCanMakeMessage() {
        if (httpVersion == null || statusCode == null) {
            throw new RequestProcessingException("주요 응답 필드가 비어있어 응답 메세지를 생성할 수 없습니다.");
        }
    }

    private String getMessage() {
        validateCanMakeMessage();
        String startAndHeader = String.join("\r\n", makeStartLine(), makeHeaderLines());
        if (body != null && !body.isEmpty()) {
            return String.join("\r\n", startAndHeader, "", body);
        }
        return startAndHeader;
    }
}
