package org.apache.coyote.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpCookie {
    private final Map<String, String> items;

    public HttpCookie(Map<String, String> items) {
        this.items = items;
    }

    public static HttpCookie from(String string) {
        if (string == null) {
            return new HttpCookie(new HashMap<>());
        }
        return Arrays.stream(string.split(" "))
                .map(line -> line.split("="))
                .collect(collectingAndThen(
                        toMap(line -> line[0], line -> line[1]),
                        HttpCookie::new
                ));
    }

    public String get(String key) {
        return items.get(key);
    }

    public String generateCookie() {
        return "JSESSIONID=" + UUID.randomUUID();
    }
}
