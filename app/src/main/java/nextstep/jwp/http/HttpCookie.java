package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie() {
        cookies = new HashMap<>();
    }

    public HttpCookie(String rawCookie) {
        cookies = Stream.of(rawCookie.split(";"))
                .map(x -> x.trim().split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    public String get(String key) {
        return cookies.get(key);
    }
}

