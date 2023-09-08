package nextstep.jwp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpCookie(Collections.emptyMap());
        }

        final Map<String, String> cookieMap = Arrays.stream(cookieHeader.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0].trim(), cookie -> cookie[1].trim()));

        return new HttpCookie(cookieMap);
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
