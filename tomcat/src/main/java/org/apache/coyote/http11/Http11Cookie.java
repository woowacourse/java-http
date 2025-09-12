package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Http11Cookie {
    private final String name;
    private final String value;

    public Http11Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Map<String, Http11Cookie> parse(String headerValue) {
        Map<String, Http11Cookie> map = new HashMap<>();
        if (headerValue == null || headerValue.isBlank()) return map;

        String[] pairs = headerValue.split(";");
        for (String pair : pairs) {
            String[] nv = pair.trim().split("=", 2);
            if (nv.length == 2) {
                String n = nv[0].trim();
                String v = nv[1].trim();
                if (!n.isEmpty()) map.put(n, new Http11Cookie(n, v));
            }
        }
        return map;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
}
