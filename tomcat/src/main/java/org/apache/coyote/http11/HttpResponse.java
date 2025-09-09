package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private int status = 200;
    private String reason = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];
    private boolean committed = false;

    public static byte[] bytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public void setStatus(
            int status,
            String reason
    ) {
        this.status = status;
        this.reason = reason;
    }

    public void setHeader(
            String name,
            String value
    ) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        this.body = body != null ? body : new byte[0];
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isCommitted() {
        return committed;
    }

    void markCommitted() {
        committed = true;
    }
}

