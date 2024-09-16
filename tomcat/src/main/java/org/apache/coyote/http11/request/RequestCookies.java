package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.constant.HeaderKey;

public class RequestCookies {

    private static final String DELIMITER_OF_COOKIES = "; ";
    private static final String KEY_OF_LOGIN_COOKIE = "JSESSIONID";
    private static final HeaderKey HEADER_KEY_OF_REQUEST_COOKIE = HeaderKey.COOKIE;

    private final List<RequestCookie> cookies = new ArrayList<>();

    public RequestCookies(RequestHeaders headers) {
        headers.get(HEADER_KEY_OF_REQUEST_COOKIE)
                .ifPresent(header -> addCookies(header.getValue()));
    }

    private void addCookies(String raw) {
        Arrays.stream(raw.split(DELIMITER_OF_COOKIES))
                .map(RequestCookie::new)
                .forEach(this.cookies::add);
    }

    public Optional<RequestCookie> getLoginCookie() {
        return get(KEY_OF_LOGIN_COOKIE);
    }

    public Optional<RequestCookie> get(String key) {
        return cookies.stream()
                .filter(cookie -> cookie.hasKey(key))
                .findAny();
    }
}
