package org.apache.coyote.http.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_PAIR = 2;

    private final Map<String, String> values;

    //TODO: 쿠키 사용해서 JSESSIONID 쿠키 생성, JSESSIONID 검증

    private HttpCookies(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookies from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            throw new IllegalArgumentException("쿠키 헤더가 비어있습니다.");
        }

        return new HttpCookies(Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.trim().split(KEY_DELIMITER, KEY_VALUE_PAIR))
                .collect(Collectors.toMap(
                        result -> result[KEY_INDEX],
                        result -> result[VALUE_INDEX]
                ))
        );
    }

    public String getJsessionid() {
        return values.get(JSESSIONID);
    }
}
