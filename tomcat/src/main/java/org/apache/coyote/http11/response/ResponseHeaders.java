package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.domain.HttpCookies;

public record ResponseHeaders(LinkedHashMap<String, String> headers, HttpCookies cookies) {

    public ResponseHeaders() {
        this(new LinkedHashMap<>(), new HttpCookies());
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public byte[] getHeader() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        cookies.appendSetCookieHeaders(sb);

        sb.append("\r\n");
        return sb.toString().getBytes();
    }
}
