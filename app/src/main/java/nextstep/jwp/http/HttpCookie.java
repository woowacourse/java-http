package nextstep.jwp.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> headers) {
        this.cookies = getExistCookies(headers);
    }

    private Map<String, String> getExistCookies(final Map<String, String> headers) {
        if (headers.containsKey(COOKIE_HEADER)) {
            return parseCookies(headers.get(COOKIE_HEADER));
        }
        return new HashMap<>();
    }

    private Map<String, String> parseCookies(final String cookie) {
        String[] split = cookie.split("; ", 2);
        return Arrays.stream(split)
                .map(cookieSet -> cookieSet.split("=", 2))
                .collect(Collectors.toMap(parsedCookie -> parsedCookie[0], parsedCookie -> parsedCookie[1]));
    }

    public boolean containsJSession() {
        return cookies.containsKey(JSESSIONID);
    }

    public void setCookies(String randomId) {
        this.cookies.put(JSESSIONID, randomId);
    }

    @Override
    public String toString() {
        return cookies.entrySet()
                .stream()
                .map(entry -> String.join("=", List.of(entry.getKey(), entry.getValue())))
                .collect(Collectors.joining("; "));
    }

    public String getJSessionCookie() {
        if (cookies.containsKey(JSESSIONID)) {
            return cookies.get(JSESSIONID);
        }
        throw new IllegalArgumentException("쿠키가 존재하지 않습니다.");
    }
}
