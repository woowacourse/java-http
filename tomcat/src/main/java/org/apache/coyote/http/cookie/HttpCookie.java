package org.apache.coyote.http.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String rawCookies) {
        if (rawCookies == null || !rawCookies.contains("JSESSIONID")) {
            throw new IllegalArgumentException("쿠키 값이 비어있습니다.");
        }

        return new HttpCookie(Arrays.stream(rawCookies.split(";"))
                .map(cookie -> cookie.trim().split("=", 2))
                .collect(Collectors.toMap(
                        result -> result[0],
                        result -> result[1]
                ))
        );
    }
}
