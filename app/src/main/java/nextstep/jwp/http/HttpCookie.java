package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {
    public static final String SESSION_NAME = "JSESSIONID";
    private final Map<String, String> cookies;

    public static HttpCookie of(String line) {
        String[] tokens = line.split(": ");
        Map<String, String> cookieMap = Stream.of(tokens[1].split("; "))
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
}
