package nextstep.jwp.http.session;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.jwp.http.session.HttpSession.SESSION_NAME;

public class HttpCookie {

    public static final String COOKIE_NAME = "Cookie";

    private final Map<String, String> cookies;

    public static HttpCookie of(String line) {
        Map<String, String> cookieMap = Stream.of(line.split("; "))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(y -> y[0], y -> y[1]));
        return new HttpCookie(cookieMap);
    }

    public HttpCookie() {
        this.cookies = new LinkedHashMap<>();
    }

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean hasSessionId() {
        return this.cookies.containsKey(SESSION_NAME);
    }

    public String getSessionId() {
        return this.cookies.get(SESSION_NAME);
    }
}
