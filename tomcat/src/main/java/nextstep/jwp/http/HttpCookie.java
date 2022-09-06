package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private static final String SESSION_ID_KEY = "JSESSIONID";

    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookies) {
        return new HttpCookie(Arrays.stream(cookies.split(COOKIES_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_DELIMITER))
                .collect(Collectors.toMap(e -> e[COOKIE_KEY_INDEX], e -> e[COOKIE_VALUE_INDEX])));
    }

    public static HttpCookie empty() {
        return new HttpCookie(new ConcurrentHashMap<>());
    }

    public boolean isEmptySessionId() {
        return values.containsKey(SESSION_ID_KEY);
    }

    public void addSessionId(final String sessionId) {
        values.put(SESSION_ID_KEY, sessionId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpCookie that = (HttpCookie) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
