package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeaders(String location) {
        headers.put("Location", location);
    }

    public String getLocation() {
        return headers.get("Location");
    }

    public void setCookie(String cookie) {
        headers.put("Set-Cookie", cookie);
    }

    public void setContentType(String contentType) {
        if ("text/html".equals(contentType)) {
            headers.put("Content-Type", contentType + ";charset=utf-8");
            return;
        }
        headers.put("Content-Type", contentType);
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", String.valueOf(length));
    }

    public String getMessage() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
