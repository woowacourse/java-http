package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

public class Cookies {
    private static final String ID = "JSESSIONID";

    private final Map<String, String> cookies;

    private Cookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookies ofJSessionId(final String id) {
        return new Cookies(Map.of(ID, id));
    }

    public static Cookies from(final String rowCookieData) {
        final Map<String, String> cookies = new HashMap<>();
        if (Objects.isNull(rowCookieData)) {
            return new Cookies(cookies);
        }
        for (final String cookie : rowCookieData.split("; ")) {
            final String[] keyAndValue = cookie.split("=");
            cookies.put(keyAndValue[0], keyAndValue[1]);
        }
        return new Cookies(cookies);
    }

    public boolean hasCookieByJSessionId() {
        return cookies.entrySet().stream()
                .anyMatch(it -> it.getKey().equalsIgnoreCase(ID));
    }

    public Optional<String> getJSessionId() {
        return cookies.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase(ID))
                .map(Entry::getValue)
                .findFirst();
    }

    public String getCookieLine() {
        return "Set-Cookie: " + ID + "=" + cookies.entrySet()
                .stream()
                .filter(it -> it.getKey().equalsIgnoreCase(ID))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("JSESSIONID 에 해당하는 쿠키가 존재하지 않습니다."));
    }
}
