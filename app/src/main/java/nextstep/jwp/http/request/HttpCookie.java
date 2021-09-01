package nextstep.jwp.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {
    public static final HttpCookie EMPTY = new HttpCookie(Collections.emptyMap());

    private final Map<String, String> params;

    private HttpCookie(Map<String, String> params) {
        this.params = params;
    }

    public static HttpCookie of(String cookieLine) {
        if(Objects.isNull(cookieLine) || cookieLine.isEmpty()){
            return EMPTY;
        }

        Map<String, String> map = new HashMap<>();
        for (String item : cookieLine.split("; ")) {
            String[] pair = item.split("=");
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
