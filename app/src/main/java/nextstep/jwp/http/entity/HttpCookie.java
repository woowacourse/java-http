package nextstep.jwp.http.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie of(String line) {
        if (line == null) {
            return HttpCookie.empty();
        }
        Map<String, String> map = new HashMap<>();
        String[] cookies = line.split("; ");
        for (String cookie : cookies) {
            String[] split = cookie.split("=", 2);
            map.put(split[0], split[1]);
        }
        return new HttpCookie(map);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
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
        return Objects.equals(cookies, that.cookies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cookies);
    }
}
