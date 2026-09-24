package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(final String cookieHeader) {
        if (cookieHeader == null) {
            return;
        }
        for (String cookie : cookieHeader.split(";")) {
            final String[] nameAndValue = cookie.trim().split("=", 2);
            if (nameAndValue.length == 2) {
                values.put(nameAndValue[0].trim(), nameAndValue[1].trim());
            }
        }
    }

    public String getValue(final String name) {
        return values.get(name);
    }
}
