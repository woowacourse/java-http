package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.domain.HttpCookies;

public record ResponseHeaders(LinkedHashMap<String, String> headers) {

    public ResponseHeaders() {
        this(new LinkedHashMap<>());
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void put(HttpCookies cookies) {
        headers.put("Set-Cookie", cookies.getJsessionid());
    }

    public byte[] getHeader() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        stringBuilder.append("\r\n");
        return stringBuilder.toString().getBytes();
    }
}
