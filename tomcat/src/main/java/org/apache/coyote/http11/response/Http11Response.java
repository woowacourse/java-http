package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class Http11Response {

    private int statusCode;
    private String resourcePath;
    private LinkedHashMap<String, String> headers;
    private byte[] body;

    public Http11Response(String resourcePath) {
        this.statusCode = 200; // 기본값
        this.headers = new LinkedHashMap<>();
        this.body = new byte[0];
        this.resourcePath = resourcePath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public byte[] getResponseLine() {
        String requestLines = "HTTP/1.1 " + statusCode + " " + getStatusText() + " \r\n";
        return requestLines.getBytes();
    }

    public byte[] getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        stringBuilder.append("\r\n");
        return stringBuilder.toString().getBytes();
    }

    private String getStatusText() {
        return switch (statusCode) {
            case 200 -> "OK";
            case 401 -> "Unauthorized";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown Status";
        };
    }
}
