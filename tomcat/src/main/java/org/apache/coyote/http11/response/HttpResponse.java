package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    
    private final HttpStatus status;
    private final String body;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public static HttpResponse ok(String body, MimeType mimeType) {
        HttpResponse response = new HttpResponse(HttpStatus.OK, body);
        response.addHeader("Content-Type", mimeType.getValue());
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        return response;
    }

    public static HttpResponse notFound() {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND, "404 Not Found");
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", String.valueOf("404 Not Found".getBytes().length));
        return response;
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, "");
        response.addHeader("Location", location);
        return response;
    }

    public String toHttpResponse() {
        StringBuilder response = new StringBuilder();
        response.append(status.getStatusLine()).append("\r\n");
        
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        
        response.append("\r\n").append(body);
        return response.toString();
    }

    public byte[] getBytes() {
        return toHttpResponse().getBytes();
    }
}