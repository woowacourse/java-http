package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseCookies {

    private final Map<String, String> cookies = new HashMap<>();

    public void addCookie(final String cookieName, final String cookieValue) {
        cookies.put(cookieName, cookieValue);
    }

    public String convertResponseCookiesMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        cookies.forEach((name, value) ->
                stringBuilder
                        .append("Set-Cookie: ")
                        .append(name)
                        .append("=")
                        .append(value).append("\r\n"));
        return stringBuilder.toString();
    }
}
