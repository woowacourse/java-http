package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private String version;
    private int statusCode;
    private String statusMessage;
    private Map<String, Object> headers;
    private String body;

    public HttpResponse(String version, int statusCode, String statusMessage, Map<String, Object> headers,
                        String body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {
        headers = new HashMap<>();
    }

    public void addVersion(String version) {
        this.version = version;
    }

    public void addStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void addStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void addBody(String body) {
        this.body = body;
    }

    public void send(OutputStream outputStream) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(version).append(" ").append(statusCode).append(" ").append(statusMessage).append(" \r\n");
            for (Entry<String, Object> entry : headers.entrySet()) {
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append(" \r\n");
            }
            sb.append("\r\n");
            sb.append(body);
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
