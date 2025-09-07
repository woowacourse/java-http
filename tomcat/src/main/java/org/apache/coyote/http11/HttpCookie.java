package org.apache.coyote.http11;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    Map<String, String> cookies;

    public HttpCookie(String cookieValues) {
        if (cookieValues == null) {
            this.cookies = Collections.emptyMap();
            return;
        }

        Map<String, String> cookies = new LinkedHashMap<>();
        String[] splits = cookieValues.split("; ");
        for (String split : splits) {
            String[] cookieEntry = split.split("=");
            cookies.put(cookieEntry[0], cookieEntry[1]);
        }
        this.cookies = cookies;
    }

    public boolean hasJsessionId() {
        return cookies.keySet().stream()
                .anyMatch(cookie -> cookie.equals("JSESSIONID"));
    }
}
