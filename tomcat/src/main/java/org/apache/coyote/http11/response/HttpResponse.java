package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_ENCODING_TYPE = "charset=utf-8";

    private StatusLine statusLine;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        statusLine = new StatusLine();
        headers = new LinkedHashMap<>();
        body = new byte[0];
    }

    public HttpResponse(StatusLine statusLine, Map<String, String> headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, byte[] body) {
        this(new StatusLine(httpStatus), headers, body);
    }

    public HttpStatus getStatus() {
        return this.statusLine.getHttpStatus();
    }

    public void setStatus(HttpStatus httpStatus) {
        this.statusLine.setHttpStatus(httpStatus);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void addHeader(byte[] bytes) {
        headers.put("Content-Type", DEFAULT_CONTENT_TYPE + ";" + DEFAULT_ENCODING_TYPE);
        headers.put("Content-Length", String.valueOf(bytes.length));
    }

    public void addHeader(String contentType, byte[] bytes) {
        headers.put("Content-Type", contentType + ";" + DEFAULT_ENCODING_TYPE);
        headers.put("Content-Length", String.valueOf(bytes.length));
    }

    public void addHeader() {
        headers.put("Content-Type", DEFAULT_CONTENT_TYPE + ";" + DEFAULT_ENCODING_TYPE);
        headers.put("Content-Length", String.valueOf(0));
    }

    public void addHeader(Map<String, String> additionalHeaders) {
        headers.putAll(additionalHeaders);
        headers.put("Content-Type", DEFAULT_CONTENT_TYPE + ";" + DEFAULT_ENCODING_TYPE);
        headers.put("Content-Length", String.valueOf(0));
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();

        sb.append(statusLine.toString()).append(" \r\n");

        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        sb.append("\r\n");

        byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (body == null) {
            return headerBytes;
        }

        byte[] result = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(body, 0, result, headerBytes.length, body.length);

        return result;
    }
}
