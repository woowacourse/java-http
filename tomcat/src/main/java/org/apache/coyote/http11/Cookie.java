package org.apache.coyote.http11;

import static org.apache.coyote.http11.Constants.NAME_INDEX;
import static org.apache.coyote.http11.Constants.VALID_PARAMETER_PAIR_LENGTH;
import static org.apache.coyote.http11.Constants.VALUE_INDEX;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Cookie(String cookieHeader) {
        this.cookies = Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .map(cookies -> cookies.split(COOKIE_SEPARATOR))
                .filter(cookie -> cookie.length == VALID_PARAMETER_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        cookie -> cookie[NAME_INDEX],
                        cookie -> cookie[VALUE_INDEX])
                );
    }

    public static Cookie ofJSessionId(String id) {
        return new Cookie(Map.of(JSESSIONID, id));
    }

    public static Cookie empty() {
        return new Cookie(Collections.emptyMap());
    }

    public String toCookieHeader() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + COOKIE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public Optional<String> getJSessionId() {
        return Optional.ofNullable(cookies.get(JSESSIONID));
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
