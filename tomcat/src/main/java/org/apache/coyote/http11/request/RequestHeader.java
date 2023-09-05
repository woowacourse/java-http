package org.apache.coyote.http11.request;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestHeader {
    private final Map<String, String> header;
    private final Cookies cookies;

    private RequestHeader(final Map<String, String> header, final Cookies cookies) {
        this.header = header;
        this.cookies = cookies;
    }

    public static RequestHeader from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> result = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] split = line.split(": ", 2);
            result.put(split[0], split[1]);
            line = bufferedReader.readLine();
        }
        return new RequestHeader(result, Cookies.from(result.get("Cookie")));
    }

    public String getHeaderValue(final String key) {
        return header.get(key);
    }

    public boolean containsKey(final String key) {
        return header.containsKey(key);
    }

    public Optional<Cookie> findCookie(final String cookieKey) {
        return cookies.getCookieOf(cookieKey);
    }
}
