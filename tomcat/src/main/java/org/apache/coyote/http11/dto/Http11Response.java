package org.apache.coyote.http11.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public record Http11Response(
        int statusCode,
        LinkedHashMap<String, String> header,
        byte[] body
) {

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public byte[] getRequestLine() {
        String requestLines = "HTTP/1.1 " + statusCode + " " + getStatusText() + " \r\n";
        return requestLines.getBytes();
    }

    public byte[] getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
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
