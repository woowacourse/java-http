package com.techcourse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    private final UUID id = UUID.randomUUID();


    public HttpCookie(String rawValue) {
        String[] split = rawValue.split(";");
        for (int i = 0; i < split.length; i++) {
            String[] split2 = split[i].split("=");
            cookies.put(split2[0], split2[1]);
        }
    }

    public boolean existsJssessionId() {
        return cookies.containsKey("JSSESSIONID");
    }

    public String getJssessionId() {
        return String.valueOf(id);
    }
}
