package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie parse(String cookieLine) {
        Map<String, String> cookies = new LinkedHashMap<>();
        List<String> dividedCookies = List.of(cookieLine.split("; "));
        for (String cookie : dividedCookies) {
            List<String> dividedCookie = List.of(cookie.split("=", 2));
            cookies.put(dividedCookie.get(0), dividedCookie.get(1));
        }
        return new Cookie(cookies);
    }

    public Optional<String> getJSessionId() {
        if (cookies.containsKey(JSESSIONID_KEY)) {
            return Optional.ofNullable(cookies.get(JSESSIONID_KEY));
        }
        return Optional.empty();
    }
}
