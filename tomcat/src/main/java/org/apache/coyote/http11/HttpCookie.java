package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(List<String> request) {
        Optional<String> header = request.stream()
                .filter(r -> r.startsWith("Cookie"))
                .findAny();

        if (header.isEmpty()) {
            return new HttpCookie(new HashMap<>());
        }

        String rawCookies = header.get()
                .split("Cookie:")[1];
        Map<String, String> cookies = Arrays.stream(rawCookies.replaceAll(" ", "").split(";"))
                .collect(Collectors.toMap(cookie -> cookie.split("=")[0], cookie -> cookie.split("=")[1]));

        return new HttpCookie(cookies);
    }

    public boolean isContains(String cookieName) {
        return cookies.containsKey(cookieName);
    }

}
