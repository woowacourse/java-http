package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private Map<String, String> cookies;

    public Cookie() {
        cookies = new LinkedHashMap<>();
    }

    public Cookie(String cookie) {
        this.cookies = Arrays.stream(cookie.split(";"))
                .filter(param -> param.contains("="))
                .map(param -> param.split("=", 2))  // 2로 제한해서 key=value 형태로 분리
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim()
                ));
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public Map<String, String> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
