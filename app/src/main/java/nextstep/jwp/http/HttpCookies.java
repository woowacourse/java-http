package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookies(String cookiesString) {
        parseCookies(cookiesString);
    }

    public void parseCookies(String cookiesString) {
        String[] cookiesStrings = cookiesString.split(";");
        for (String cookie : cookiesStrings) {
            int equalIndex = cookie.indexOf("=");
            String key = cookie.substring(0, equalIndex);
            String value = cookie.substring(equalIndex + 1);
            cookies.put(key.trim(), value.trim());
        }
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public boolean hasKey(String key) {
        return cookies.containsKey(key);
    }

    public String getCookie(String cookie) {
        return cookies.get(cookie);
    }
}
