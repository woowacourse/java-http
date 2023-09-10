package nextstep.jwp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private final Map<String, String> cookies = new LinkedHashMap<>();

    public HttpCookies() {
    }

    public HttpCookies(final Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookies from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpCookies(Collections.emptyMap());
        }

        final Map<String, String> cookieMap = Arrays.stream(cookieHeader.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0].trim(), cookie -> cookie[1].trim()));

        return new HttpCookies(cookieMap);
    }

    public void save(final String cookie, final String value) {
        cookies.put(cookie, value);
    }

    public String ofSessionId(final String sessionId) {
        return cookies.get(sessionId);
    }

    public String cookieInfo(final String sessionId) {
        return sessionId + "=" + cookies.get(sessionId);
    }
}
