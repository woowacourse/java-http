package nextstep.jwp.request;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.Http;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String cookie) {
        this.cookies = parseCookie(cookie);
    }

    private Map<String, String> parseCookie(String cookie) {
        return Stream.of(cookie.split(Http.SEMI_COLUMN_SEPARATOR))
                .map(item -> item.trim().split(Http.EQUAL_SEPARATOR))
                .collect(Collectors.toMap(x -> x[Http.KEY].trim(), x -> x[Http.VALUE].trim()));
    }

    public boolean contains(String key){
        return cookies.containsKey(key);
    }

    public String get(String key){
        return cookies.get(key);
    }
}
