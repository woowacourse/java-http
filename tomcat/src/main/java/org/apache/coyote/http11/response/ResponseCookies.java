package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ResponseCookies {

    private static final String DELIMITER_OF_COOKIE = "; ";
    private static final String FORMAT_OF_COOKIE_HEADER = "Set-Cookie: %s";
    private static final String KEY_OF_LOGIN_COOKIE = "JSESSIONID";

    private final List<ResponseCookie> cookies = new ArrayList<>();

    public ResponseCookies() {
    }

    public void addLoginCookie(String newSessionId) {
        add(KEY_OF_LOGIN_COOKIE, newSessionId);
    }

    private void add(String key, String value) {
        cookies.add(new ResponseCookie(key, value));
    }

    public void buildHttpMessage(StringJoiner messageJoiner) {
        if (cookies.isEmpty()) {
            return;
        }
        messageJoiner.add(String.format(FORMAT_OF_COOKIE_HEADER, cookies.stream()
                .map(ResponseCookie::buildHttpMessage)
                .collect(Collectors.joining(DELIMITER_OF_COOKIE))));
    }
}
