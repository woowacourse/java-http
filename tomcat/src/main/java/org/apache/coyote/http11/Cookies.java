package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private final Map<String, String> cookies;

    public Cookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Cookies(final String cookie) {
        final List<String> keyValues = Arrays.stream(cookie.split(";")).map(String::trim).toList();
        this.cookies = keyValues.stream()
                .map(keyValue -> keyValue.split("="))
                .collect(
                        Collectors.toMap(
                                keyValueSplit -> keyValueSplit[0],
                                keyValueSplit -> keyValueSplit[1]
                        )
                );
    }

    public String toCookieList() {
        final List<String> cookieList = cookies.entrySet().stream()
                .map(cookie -> String.format("%s=%s", cookie.getKey(), cookie.getValue()))
                .toList();
        return String.join("; ", cookieList);
    }

    public String getValue(final String key) {
        return cookies.get(key);
    }

    public boolean hasValue(final String key) {
        return cookies.containsKey(key);
    }
}
