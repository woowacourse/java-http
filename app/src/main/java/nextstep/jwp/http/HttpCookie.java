package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpCookie {
    public static String J_SESSION_ID = "JSESSIONID";

    private Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie of(String cookieData) {
        return new HttpCookie(parseCookie(cookieData));
    }

    public boolean hasCookie(String key) {
        return cookies.containsKey(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public static Map<String, String> parseCookie(String cookieData) {
        Map<String, String> cookies = new HashMap<>();

        String[] data = cookieData.split(";");
        for (String each : data) {
            String[] keyAndValue = each.split("=");
            cookies.put(keyAndValue[0].trim(), keyAndValue[1].trim());
        }

        return cookies;
    }
}
