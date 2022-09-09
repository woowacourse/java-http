package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {
    private final Map<String, String> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType + ";charset=utf-8");
    }

    public void setContentLength(int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));
    }

    public void setCookie(String cookie) {
        headers.put("Set-Cookie", cookie);
    }

    public String toMessage() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
