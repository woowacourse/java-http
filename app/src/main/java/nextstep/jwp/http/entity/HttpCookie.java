package nextstep.jwp.http.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {
    private static final String SESSION_ID = "JSESSIONID";
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

    public static HttpCookie of(HttpSession httpSession) {
        return new HttpCookie(Map.of(SESSION_ID, httpSession.getId()));
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getSessionId() {
        return cookies.get(SESSION_ID);
    }

    public String asString(String key) {
        String value = cookies.get(key);
        return key + "=" + value;
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
