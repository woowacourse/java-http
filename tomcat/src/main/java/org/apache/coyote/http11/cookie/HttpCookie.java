package org.apache.coyote.http11.cookie;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

@Getter
public class HttpCookie {

    private static final String SEPARATOR = "; ";
    private static final String DELIMITER = "=";

    private Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    private HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie from(String cookies) {
        if (cookies.isEmpty()) {
            return HttpCookie.empty();
        }
        return Arrays.stream(cookies.split(SEPARATOR))
                .map(header -> header.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(header -> header[0], header -> header[1]),
                        HttpCookie::new
                ));
    }

    public static HttpCookie empty() {
        return new HttpCookie();
    }

    public void put(String key, String value) {
        cookies.put(key, value);
    }

    public String find(String key) {
        return cookies.get(key);
    }
}
