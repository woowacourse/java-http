package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(final String cookies) {
        String[] split = cookies.split(";");
        for (String cookie : split) {
            String[] keyAndValue = cookie.split("=");
            if (keyAndValue.length == 2) {
                values.put(keyAndValue[0], keyAndValue[1]);
            }
        }
    }
}
