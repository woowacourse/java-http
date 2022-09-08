package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public HttpCookie(final String rawCookie) {
        this.values = new HashMap<>();

        if (rawCookie != null) {
            String[] cookies = rawCookie.split(" ");

            for (String cookie : cookies) {
                String[] value = cookie.split(COOKIE_SEPARATOR);
                this.values.put(value[KEY_INDEX], value[VALUE_INDEX]);
            }
        }
    }

    public static HttpCookie fromJSessionId(final String sessionId) {
        return new HttpCookie(Map.of(JSESSIONID, sessionId));
    }

    public String getCookie() {
        return JSESSIONID + "=" + values.get(JSESSIONID);
    }

    public String getSessionId() {
        return values.get(JSESSIONID);
    }
}
