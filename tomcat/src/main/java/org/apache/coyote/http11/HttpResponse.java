package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;

public class HttpResponse {
    private final HttpStatus statusCode;
    private final ContentType contentType;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(HttpStatus statusCode, ContentType contentType) {
        this(statusCode, contentType, new HashMap<>(), null);
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
