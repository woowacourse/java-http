package org.apache.coyote.http11.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record HttpCookies(Map<String, String> values) {

    private static final String JSESSIONID = "JSESSIONID";

    public HttpCookies() {
        this(Map.of(JSESSIONID, UUID.randomUUID().toString()));
    }

    public static HttpCookies parse(String cookieHeader) {
        Map<String, String> values = new HashMap<>();
        String[] splitCookies = cookieHeader.split("; ");
        for (String splitCookie : splitCookies) {
            String[] keyValue = splitCookie.split("=");
            values.put(keyValue[0], keyValue[1]);
        }
        return new HttpCookies(values);
    }

    public String getJsessionid() {
        return JSESSIONID + "=" + values.get(JSESSIONID);
    }
}
