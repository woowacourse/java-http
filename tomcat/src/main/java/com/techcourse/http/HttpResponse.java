package com.techcourse.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private String statusCode = "200";
    private String statusMessage = "OK";
    private Map<String, List<String>> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse() {
    }

    public static HttpResponse of(String statusCode, String statusMessage, Map<String, List<String>> headers,
                                  byte[] body) {
        HttpResponse response = new HttpResponse();

        response.setStatus(statusCode, statusMessage);
        headers.forEach(response::setHeader);
        response.setBody(body);

        return response;

    }

    public void redirect(String url) {
        setStatus("302", "FOUND");
        setHeader("Location", List.of(url));
        setBody(new byte[0]);
    }

    public byte[] toBytes() {
        byte[] responseBody = body == null ? new byte[0] : body;
        StringBuilder header = new StringBuilder();

        // 실제 body 기준으로 설정
        setHeader("Content-Length", List.of(String.valueOf(responseBody.length)));

        header.append(PROTOCOL_VERSION)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                header.append(entry.getKey())
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }

        byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[headerBytes.length + responseBody.length];

        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(responseBody, 0, result, headerBytes.length, responseBody.length);

        return result;
    }

    public void setStatus(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setHeader(String name, List<String> values) {
        String actualName = findHeaderName(name);

        if (actualName == null) {
            actualName = name;
        }

        headers.put(actualName, new ArrayList<>(values));
    }

    public void addHeader(String name, List<String> values) {
        String actualName = findHeaderName(name);

        if (actualName == null) {
            actualName = name;
        }

        headers.computeIfAbsent(actualName, ignored -> new ArrayList<>())
                .addAll(values);
    }

    public boolean hasHeader(String name) {
        return findHeaderName(name) != null;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    private String findHeaderName(String name) {
        return headers.keySet()
                .stream()
                .filter(headerName -> headerName.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
