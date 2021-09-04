package nextstep.jwp.request;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.exception.UnauthorizedException;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String cookie) {
        this.cookies = parseCookie(cookie);
    }

    private Map<String, String> parseCookie(String cookie) {
        return Stream.of(cookie.split(HttpTerms.SEMI_COLUMN_SEPARATOR))
                .map(item -> item.trim().split(HttpTerms.EQUAL_SEPARATOR))
                .collect(Collectors.toMap(x -> x[HttpTerms.KEY].trim(), x -> x[HttpTerms.VALUE].trim()));
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public static String toSetCookieValue(String sessionId) {
        return HttpTerms.JSESSIONID + HttpTerms.EQUAL_SEPARATOR + sessionId;
    }
}
