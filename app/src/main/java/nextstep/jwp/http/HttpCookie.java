package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final int COOKIE_KEY = 0;
    private static final int COOKIE_VALUE = 1;
    private final Map<String, String> cookie;

    public HttpCookie(final String cookieData) {
        cookie = setCookie(cookieData);
    }

    private Map<String, String> setCookie(final String cookieRawData) {
        Map<String, String> cookieMap = new HashMap<>();
        String[] cookieDataSet = cookieRawData.split(";");
        for (String cookieData : cookieDataSet) {
            cookieData = cookieData.trim();
            String[] cookieDatas = cookieData.split("=");
            cookieMap.put(cookieDatas[COOKIE_KEY], cookieDatas[COOKIE_VALUE]);
        }
        return cookieMap;
    }

    public boolean containsKey(final String cookieKey) {
        return cookie.containsKey(cookieKey);
    }

    public String getCookieValueByKey(final String cookieKey) {
        return cookie.get(cookieKey);
    }
}
