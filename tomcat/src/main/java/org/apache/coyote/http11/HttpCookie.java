package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = ";";
    private static final String DELIMITER = "=";
    // String, String으로 쿠키를 만들지
    // Cookie 클래스를 쓸지
    private final Map<String, String> cookies;

    // 쿠키에 ;랑 =가 들어가는데, 어떻게 구분해야 할까? 얘네 안에는 이것들이 포함되면 어떻게되지?
    // ;는 원칙적으로 key value로 사용하지 못한다.
    // =는 jwt에서도 값에 포함될 수 있음. 그래서 가장 먼저 만나는 =를 기준으로 파싱하면 된다.

    public HttpCookie(String cookieLine) {
        if (cookieLine == null) {
            cookies = new HashMap<>();
            return;
        }
        String[] tokens = cookieLine.split(COOKIE_DELIMITER);
        Map<String, String> map = new HashMap<>();
        for (String token : tokens) {
            String strip = token.strip();
            int delimiterIndex = strip.indexOf(DELIMITER);
            String key = strip.substring(0, delimiterIndex);
            String value = strip.substring(delimiterIndex + 1);
            map.put(key, value);
        }
        cookies = map;
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
