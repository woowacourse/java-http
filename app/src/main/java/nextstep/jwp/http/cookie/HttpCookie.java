package nextstep.jwp.http.cookie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {

    // TODO :: RESPONSE_FORMAT 에 너무 엮이는 것은 아닐까

    public static final HttpCookie EMPTY = new HttpCookie(Collections.emptyMap());

    private static final String KEY_SESSION_ID = "JSESSIONID";
    private static final String ITEM_SEPARATOR = "; ";
    private static final String KEY_SEPARATOR = "=";

    private final Map<String, String> params;

    public HttpCookie(Map<String, String> params) {
        this.params = params;
    }

    public static HttpCookie of(String cookieLine) {
        if (Objects.isNull(cookieLine) || cookieLine.isEmpty()) {
            return EMPTY;
        }

        Map<String, String> map = new HashMap<>();
        for (String item : cookieLine.split(ITEM_SEPARATOR)) {
            String[] pair = item.split(KEY_SEPARATOR);
            if (pair.length < 2) {
                break;
            }
            map.put(pair[0], pair[1]);
        }
        return new HttpCookie(map);
    }

    public String getAttributes(String name) {
        return params.get(name);
    }

    public String getSessionId() {
        return params.get(KEY_SESSION_ID);
    }

    public boolean hasSessionId() {
        return params.containsKey(KEY_SESSION_ID);
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
