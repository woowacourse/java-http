package org.apache.coyote.http11.model;

import org.apache.coyote.http11.controller.StaticResourceController;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String version;
    private StatusCode statusCode;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.version = "HTTP/1.1";
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        this.headers = new HashMap<>();
        setHeader("Content-Type", StaticResourceController.DEFAULT_CONTENT_TYPE);
        setHeader("Content-Length", "0");
        this.body = "";
    }

    public byte[] getBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode.getStatusCode()).append(" ").append(statusCode.getMessage()).append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        if(body != null) {
            sb.append(body);
        }

        return sb.toString().getBytes();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addCookie(String setCookie) {
        setHeader("Set-Cookie", setCookie);
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void setBodyAndContentLength(String body) {
        this.body = body;
        setHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public void sendRedirect(String location) {
        setHeader("Location", location);
    }

    private void setHeader(String name, String value) {
        headers.put(name, value);
    }
}
