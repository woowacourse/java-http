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

    private final Map<String, String> values;

    public HttpCookie(String cookie) {
        this.values = Stream.of(cookie.split(COOKIES_DELIMITER))
                .map(x -> x.split(COOKIE_VALUE_DELIMITER))
                .collect(Collectors.toMap(splitCookie -> splitCookie[KEY].trim(), splitCookie -> splitCookie[VALUE].trim()));
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }

    public int size() {
        return values.size();
    }

    public Map<String, String> getValues() {
        return values;
    }
}
