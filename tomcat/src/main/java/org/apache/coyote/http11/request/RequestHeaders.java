package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.domain.HttpCookies;

public record RequestHeaders(LinkedHashMap<String, String> headers) {

    public static RequestHeaders parse(final List<String> headerLines) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        for (final String line : headerLines) {
            final String[] header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }

        return new RequestHeaders(headers);
    }

    public int getContentLength() {
        final String contentLength = "Content-Length";
        return headers.containsKey(contentLength) ? Integer.parseInt(headers.get(contentLength)) : 0;
    }

    public HttpCookies getCookies() {
        final String cookieName = "Cookie";
        if (headers.containsKey(cookieName)) {
            String cookie = headers.get(cookieName);
            return parseToHttpCookies(cookie);
        }
        return null;
    }

    private static HttpCookies parseToHttpCookies(String cookieHeader) {
        Map<String, String> values = new HashMap<>();
        String[] splitCookies = cookieHeader.split("; ");
        for (String splitCookie : splitCookies) {
            String[] keyValue = splitCookie.split("=");
            values.put(keyValue[0], keyValue[1]);
        }
        return new HttpCookies(values);
    }
}
