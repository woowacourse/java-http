package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    public static HttpCookie parseHttpCookie(final String cookie) {
        final List<String> keyValues = Arrays.stream(cookie.split(";")).map(String::trim).toList();
        final Map<String, String> cookieMap = keyValues.stream()
                .map(keyValue -> keyValue.split("="))
                .collect(
                        Collectors.toMap(
                                keyValueSplit -> keyValueSplit[0],
                                keyValueSplit -> keyValueSplit[1]
                        )
                );
        return new HttpCookie(cookieMap);
    }

    private HttpRequestParser() {
    }
}
