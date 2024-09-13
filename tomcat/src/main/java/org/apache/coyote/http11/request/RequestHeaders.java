package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.utils.Separator;

public class RequestHeaders {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

    private Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getCookieValue(String cookieName) {
        String rawCookies = headers.get("Cookie");
        if (rawCookies == null) {
            throw new IllegalArgumentException("Cookie header not exists.");
        }

        List<String> cookies = Arrays.stream(rawCookies.split(COOKIE_SEPARATOR)).toList();
        Map<String, String> separatedCookies = Separator.separateKeyValueBy(cookies, COOKIE_KEY_VALUE_SEPARATOR);
        String result = separatedCookies.get(cookieName);

        if (result == null) {
            throw new IllegalArgumentException("No such Cookie.");
        }
        return result;
    }
}
