package nextstep.jwp.domain;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIES_DELIMITER = ";";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> cookieMap;

    public HttpCookie(Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> cookieMap = Stream.of(cookies.split(COOKIES_DELIMITER))
                .map(x -> x.split(COOKIE_VALUE_DELIMITER))
                .collect(Collectors.toMap(splitCookie -> splitCookie[KEY].trim(), splitCookie -> splitCookie[VALUE].trim()));
        return new HttpCookie(cookieMap);
    }

    public String getSessionId() {
        return cookieMap.get(JSESSIONID);
    }

    public int size() {
        return cookieMap.size();
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }
}
