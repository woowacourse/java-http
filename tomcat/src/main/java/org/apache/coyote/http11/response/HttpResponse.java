package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private final String version;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, Object> headers;
    private final String body;

    public HttpResponse(String version, int statusCode, String statusMessage, Map<String, Object> headers,
                        String body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(String version, int statusCode,
                                    String contentType, String body) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", body.getBytes().length);

        return new HttpResponse(version, statusCode, "OK", headers, body);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
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
