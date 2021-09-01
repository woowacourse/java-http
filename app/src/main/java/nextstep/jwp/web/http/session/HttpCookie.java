package nextstep.jwp.web.http.session;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_FORMAT = "%s%s%s";
    private static final String COOKIE_DELIMITER_FORMAT = "%s ";
    private static final String EMPTY = "";
    private static final String SESSION_ID = "JSESSIONID";
    private static final int KEY_VALUE_ARRAY_MIN_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookie;

    public HttpCookie(String rawCookie) {
        if (rawCookie.isEmpty()) {
            this.cookie = new HashMap<>();
            return;
        }
        this.cookie = Arrays.stream(rawCookie.split(COOKIE_DELIMITER))
            .map(cookie -> cookie.split(KEY_VALUE_DELIMITER, KEY_VALUE_ARRAY_MIN_SIZE))
            .peek(arr -> {
                arr[KEY_INDEX] = arr[KEY_INDEX].trim();
                arr[VALUE_INDEX] = arr[VALUE_INDEX].trim();
            })
            .collect(toMap(arr -> arr[KEY_INDEX], arr -> arr[VALUE_INDEX]));
    }

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public void setSessionId(String sessionId) {
        cookie.put(SESSION_ID, sessionId);
    }

    public String get(String key) {
        return cookie.getOrDefault(key, EMPTY);
    }

    public boolean containsSession() {
        return cookie.containsKey(SESSION_ID);
    }

    public String getSessionId() {
        return cookie.get(SESSION_ID);
    }

    public String asString() {
        return cookie.entrySet().stream()
            .map(entry -> String
                .format(COOKIE_FORMAT, entry.getKey(), KEY_VALUE_DELIMITER, entry.getValue()))
            .collect(joining(String.format(COOKIE_DELIMITER_FORMAT, COOKIE_DELIMITER)));
    }
}
