package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.Cookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {
    private final Map<String, String> header;

    private ResponseHeaders(final Map<String, String> header) {
        this.header = header;
    }

    public static ResponseHeaders from(final ResponseBody responseBody) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Content-Type", responseBody.getContentType().getType());
        result.put("Content-Length", String.valueOf(responseBody.getContent().getBytes().length));
        return new ResponseHeaders(result);
    }

    public static ResponseHeaders redirect(final String redirectPath) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Location", redirectPath);
        return new ResponseHeaders(result);
    }

    public void addCookie(final Cookie cookie) {
        header.put("Set-Cookie", cookie.toString());
    }

    public String getCookieValues() {
        return header.get("Set-Cookie");
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue())
                    .append(" ")
                    .append("\r\n");
        }
        return result.toString();
    }
}
