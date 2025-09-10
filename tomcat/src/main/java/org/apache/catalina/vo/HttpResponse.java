package org.apache.catalina.vo;

import java.util.HashMap;
import java.util.Map;

public record HttpResponse(
        String mediaType,
        HttpStatus status,
        Map<String, String> headers,
        String body
) {

    public HttpResponse(String mediaType, HttpStatus status, String body) {
        this(mediaType, status, new HashMap<>(), body);
    }

    public void setCookie(HttpCookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public int getStatusCode() {
        return status.value();
    }

    public String getStatusReason() {
        return status.reason();
    }

    public String getHeaderString() {
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            result.append(String.format("%s: %s", key, headers.get(key)));
            result.append("\r\n");
        }
        return result.toString();
    }
}
