package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.Cookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {
    private final Map<String, String> header;

    private ResponseHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static ResponseHeader from(final ResponseBody responseBody) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Content-Type", responseBody.getContentType().getType());
        result.put("Content-Length", String.valueOf(responseBody.getContent().getBytes().length));
        return new ResponseHeader(result);
    }

    public static ResponseHeader redirect(final String redirectPath) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Location", redirectPath);
        return new ResponseHeader(result);
    }

    public void addCookie(final Cookie cookie) {
        header.put("Set-Cookie", cookie.toString());
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
