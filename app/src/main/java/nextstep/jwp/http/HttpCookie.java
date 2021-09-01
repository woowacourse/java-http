package nextstep.jwp.http;

import java.util.Map;
import java.util.NoSuchElementException;

public class HttpCookie {
    public static String J_SESSION_ID = "JSESSIONID";

    private Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie of(String cookieData) {
        return new HttpCookie(HttpUtil.parseQuery(cookieData));
    }

    public boolean hasCookie(String key) {
        return cookies.containsKey(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
