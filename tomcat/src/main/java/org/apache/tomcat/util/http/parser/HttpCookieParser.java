package org.apache.tomcat.util.http.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookieParser {

    private HttpCookieParser() {
    }

    public static Map<String, String> parseCookies(String cookies) {
        if (Objects.isNull(cookies)) {
            return new HashMap<>();
        }
        return Arrays.stream(cookies.split(";"))
                .map(pair -> pair.trim().split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }
}
