package nextstep.jwp.http.cookie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {
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
        System.out.println(cookieLine);
        for (String item : cookieLine.split(ITEM_SEPARATOR)) {
            String[] pair = item.split(KEY_SEPARATOR);
            System.out.println(pair[0] + " "+ pair[1]);
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

    public String asResponseLine() {
        return params.keySet().stream()
                .map(key -> key + KEY_SEPARATOR + params.get(key))
                .collect(Collectors.joining(ITEM_SEPARATOR));
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
