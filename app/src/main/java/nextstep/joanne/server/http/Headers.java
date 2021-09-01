package nextstep.joanne.server.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class Headers {
    private Map<String, String> headers = new LinkedHashMap<>();
    private Cookie cookie = new Cookie();

    public Headers() {
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> {
            stringBuilder.append(key).append(": ").append(value).append(" \r\n");
        });
        return stringBuilder.toString();
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
    }

    public void putCookie(String cookie) {
        this.cookie = new Cookie(cookie);
    }

    public String makeSessionId() {
        return cookie.makeSessionId();
    }

    public boolean hasSessionId() {
        return cookie.hasSessionId();
    }
}
