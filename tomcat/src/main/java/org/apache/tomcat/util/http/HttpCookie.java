package org.apache.tomcat.util.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {

    private Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookies) {
        this.cookies = parseCookies(cookies);
    }


    private Map<String, String> parseCookies(String cookies) {
        if (Objects.isNull(cookies)) {
            return new HashMap<>();
        }
        return Arrays.stream(cookies.split(";"))
                .map(pair -> pair.trim().split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }

    public String get(String cookieName) {
        return cookies.get(cookieName);
    }
}
