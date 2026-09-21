package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final String JSESSIONID = "JSESSIONID";

    private static final String COOKIE_DELIMITER = ";";
    private static final String NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new LinkedHashMap<>(cookies);
    }

    public static HttpCookie empty() {
        return new HttpCookie(Map.of());
    }

    public static HttpCookie from(String cookieHeader) {
        if (cookieHeader == null) {
            return empty();
        }

        Map<String, String> cookies = Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.trim().split(NAME_VALUE_DELIMITER, 2))
                .filter(nameAndValue -> nameAndValue.length == 2 && !nameAndValue[0].isBlank())
                .collect(Collectors.toMap(
                        nameAndValue -> nameAndValue[0].trim(),
                        nameAndValue -> nameAndValue[1].trim(),
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        return new HttpCookie(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public void add(String name, String value) {
        cookies.put(name, value);
    }

    public List<String> toHeaderValues() {
        return cookies.entrySet().stream()
                .map(cookie -> cookie.getKey() + NAME_VALUE_DELIMITER + cookie.getValue())
                .toList();
    }
}
