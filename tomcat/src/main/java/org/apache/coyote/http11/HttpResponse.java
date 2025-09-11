package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;

public class HttpResponse {
    private HttpStatus statusCode;
    private ContentType contentType;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponse() {
    }

    public HttpResponse(HttpStatus statusCode, ContentType contentType, String body) {
        this(statusCode, contentType, new HashMap<>(), body);
    }

    public HttpResponse(
            HttpStatus statusCode,
            ContentType contentType,
            Map<String, String> headers,
            String body
    ) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.headers = headers;
        this.body = body;
    }

    public void appendHeader(String key, String value) {
        headers.put(key, value);
    }

    public byte[] convertByteArray() {
        return convertString().getBytes(StandardCharsets.UTF_8);
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setResponse(HttpStatus status, ContentType contentType, String body) {
        this.statusCode = status;
        this.contentType = contentType;
        this.body = body;
    }

    public void setResponse(HttpStatus status, ContentType contentType) {
        this.statusCode = status;
        this.contentType = contentType;
    }

    public void setRedirect(String location) {
        appendHeader("Location", location);
    }

    public void setCookie(Map<String, Object> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return;
        }

        cookies.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue().toString() + "; Path=/; HttpOnly")
                .forEach(cookieString -> appendHeader("Set-Cookie", cookieString));
    }

    private String convertString() {
        return body == null ? convertNonContainsBody() : convertContainsBody();
    }

    private String convertNonContainsBody() {
        StringBuilder response = new StringBuilder();
        response.append(String.format("HTTP/1.1 %s %s", statusCode.getCode(), statusCode.getMessage())).append("\r\n");
        response.append(String.format("Content-Type: %s;charset=utf-8", contentType.getResponseContentType())).append("\r\n");
        
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(String.format("%s: %s", header.getKey(), header.getValue())).append("\r\n");
        }
        
        response.append("\r\n");
        return response.toString();
    }

    private String convertContainsBody() {
        StringBuilder response = new StringBuilder();
        response.append(String.format("HTTP/1.1 %s %s", statusCode.getCode(), statusCode.getMessage())).append("\r\n");
        response.append(String.format("Content-Type: %s;charset=utf-8", contentType.getResponseContentType())).append("\r\n");
        response.append(String.format("Content-Length: %d", body.getBytes(StandardCharsets.UTF_8).length)).append("\r\n");
        
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(String.format("%s: %s", header.getKey(), header.getValue())).append("\r\n");
        }
        
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }


}
