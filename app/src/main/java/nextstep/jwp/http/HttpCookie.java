package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCookie {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpCookie.class);

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
        if (!cookie.containsKey(cookieKey)) {
            LOGGER.error("없는 key의 쿠키정보를 찾으려고 했습니다. key = {}", cookieKey);
            throw new IllegalArgumentException(String.format("없는 key의 쿠키정보를 찾으려고 했습니다. key = {%s}", cookieKey));
        }
        return cookie.get(cookieKey);
    }
}
