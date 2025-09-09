package org.apache.coyote.http11.request;

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

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
