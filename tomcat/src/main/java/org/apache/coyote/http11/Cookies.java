package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

class Cookies {

    private final Map<String, HttpCookie> values;

    Cookies(String cookieHeader) {
        this.values = parse(cookieHeader);
    }

    private Map<String, HttpCookie> parse(String cookieHeader) {
        Map<String, HttpCookie> cookies = new LinkedHashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }

        String[] fields = cookieHeader.split(";");
        for (String field : fields) {
            putCookie(cookies, field);
        }
        return cookies;
    }

    private void putCookie(Map<String, HttpCookie> cookies, String field) {
        int separatorIndex = field.indexOf('=');
        if (separatorIndex < 0) {
            return;
        }

        String name = field.substring(0, separatorIndex).trim();
        String value = field.substring(separatorIndex + 1).trim();
        cookies.put(name, new HttpCookie(name, value));
    }

    String getValue(String name) {
        HttpCookie cookie = values.get(name);
        if (cookie == null) {
            return null;
        }
        return cookie.value();
    }
}
