package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeaders {

    private final Map<String, String> responseHeaders;
    private final Cookie cookie;

    protected ResponseHeaders() {
        this.responseHeaders = new HashMap<>();
        this.cookie = new Cookie();
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    public void addCookie(String key, String value) {
        cookie.addCookie(key, value);
    }

    public String generateMessage() {
        StringJoiner headers = new StringJoiner("\r\n");
        responseHeaders.entrySet().stream()
                .map(headerEntry -> headerEntry.getKey() + ": " + headerEntry.getValue() + " ")
                .forEach(headers::add);

        return headers.toString();
    }

    public String generateCookieMessage() {
        return cookie.generateResponseMessage();
    }

    public Cookie getCookie() {
        return cookie;
    }
}
