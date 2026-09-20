package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookies {
    private static final String PARAMETER_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";

    private Map<String, String> cookies;

    private HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String cookieHeaderValue) {
        String
    }
}
