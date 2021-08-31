package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestCookie {

    private final Map<String, String> cookies;

    public RequestCookie() {
        this.cookies = new ConcurrentHashMap<>();
    }

    public void add(String cookiesLine) {
        final String[] splitCookiesValue = cookiesLine.split("; ");
        for (String cookieValue : splitCookiesValue) {
            final String[] splitCookieValue = cookieValue.split("=");
            final String key = splitCookieValue[0];
            final String value = splitCookieValue[1];
            cookies.put(key, value);
        }
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
