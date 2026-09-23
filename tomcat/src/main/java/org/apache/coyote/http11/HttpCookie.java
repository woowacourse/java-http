package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> values;

    public HttpCookie(String cookie) {
        this.values = parseCookie(cookie);
    }

    // 아래와 같은 쿠키 값을 파싱해서 map으로 저장한다.
    // yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
    public Map<String, String> parseCookie(String cookie) {
        Map<String, String> cookieValue = new HashMap<>();
        if (cookie == null || cookie.isBlank()) {
            return new HashMap<>();
        }

        String[] tokens = cookie.split(";");
        for (String token : tokens) {
            if (token == null || token.isBlank()) {
                continue;
            }
            String[] data = token.split("=", 2);
            if (data.length != 2) {
                throw new IllegalArgumentException("쿠키 형식이 올바르지 않음");
            }
            cookieValue.put(data[0].trim(), data[1].trim());
        }
        return cookieValue;
    }

    public void add(String key, String value) {
        values.put(key, value);
    }

    public String getSessionId() {
        if (values.containsKey("JSESSIONID")) {
            values.get("JSESSIONID");
        }
        throw new IllegalArgumentException("쿠기에 JSESSIONID 정보 없음");
    }
}
