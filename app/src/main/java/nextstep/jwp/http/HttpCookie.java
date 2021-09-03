package nextstep.jwp.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public void parseExistCookies(final Map<String, String> headers) {
        if (headers.containsKey(COOKIE_HEADER)) {
            parseCookies(headers.get(COOKIE_HEADER));
        }
    }

    private void parseCookies(final String cookie) {
        String[] split = cookie.split("; ", 2);
        cookies.putAll(Arrays.stream(split)
                .map(cookieSet -> cookieSet.split("=", 2))
                .collect(Collectors.toMap(parsedCookie -> parsedCookie[0], parsedCookie -> parsedCookie[1])));
    }

    public boolean containsJSession() {
        return cookies.containsKey(JSESSIONID);
    }

    public void addJSessionCookie(String id) {
        this.cookies.put(JSESSIONID, id);
    }

    public String getJSessionCookie() {
        if (!cookies.containsKey(JSESSIONID)) {
            return "";
        }
        return cookies.get(JSESSIONID);
    }

    public HttpCookie setJSessionCookie(String key, String value) {
        cookies.computeIfPresent(key, (k, v) -> cookies.replace(k, value));
        return this;
    }

    @Override
    public String toString() {
        return cookies.entrySet()
                .stream()
                .map(entry -> String.join("=", List.of(entry.getKey(), entry.getValue())))
                .collect(Collectors.joining("; "));
    }

    public void removeJSessionCookie() {
        cookies.remove(JSESSIONID);
    }
}
