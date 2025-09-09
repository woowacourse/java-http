package org.apache.coyote.http11;

import com.techcourse.presentation.HttpRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final HttpRequest request) {
        this.cookies = extractCookie(request);
    }

    private Map<String, String> extractCookie(final HttpRequest request) {
        final Map<String, String> cookies = new LinkedHashMap<>();

        final String value = request.headers().get("Cookie");
        if (value == null) {
            return Map.of();
        }

        for (String pair : value.split(";")) {
            final int index = pair.indexOf('=');
            final String k = URLDecoder.decode(pair.substring(0, index).trim(), StandardCharsets.UTF_8);
            final String v = URLDecoder.decode(pair.substring(index + 1).trim(), StandardCharsets.UTF_8);
            cookies.put(k, v);
        }

        return Map.copyOf(cookies);
    }

    public boolean hasAttribute(final String name) {
        return cookies.containsKey(name);
    }
}
