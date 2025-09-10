package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.header.RequestHeaders;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.header.ResponseHeaders;

public class HttpCookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String REQUEST_HEADER_COOKIE_NAME = "Cookie";

    private final Map<String, String> cookies;

    public static HttpCookie from(final RequestHeaders headers) {
        final String rawCookie = headers.getOrDefault(REQUEST_HEADER_COOKIE_NAME, "");

        if (rawCookie.isBlank()) {
            return new HttpCookie(new HashMap<>());
        }

        final String[] cookiePairs = rawCookie.split(COOKIE_SEPARATOR);
        final Map<String, String> cookies = new HashMap<>();
        for (String cookiePair : cookiePairs) {
            final int separatorIndex = cookiePair.indexOf(KEY_VALUE_SEPARATOR);
            final String key = cookiePair.substring(0, separatorIndex);
            final String value = cookiePair.substring(separatorIndex + 1);
            cookies.put(key, value);
        }
        return new HttpCookie(cookies);
    }

    public static HttpCookie of(final String cookieName, final String cookieValue) {
        return new HttpCookie(Map.of(cookieName, cookieValue));
    }

    public void addToResponseHeaders(final ResponseHeaders responseHeaders) {
        cookies.forEach((key, value) -> {
            final ResponseHeader header = ResponseHeader.createSetCookieHeader(key + "=" + value);
            responseHeaders.add(header);
        });
    }

    public Optional<String> get(final String key) {
        return Optional.ofNullable(cookies.get(key));
    }

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }
}
