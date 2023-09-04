package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String JSESSION = "JSESSIONID";
    public static final String SEMI_COLON = "; ";
    public static final String EQUAL_SIGN = "=";

    private final Map<String, String> params = new HashMap<>();

    public Cookie(String cookie) {
        String[] paramsSplit = cookie.split(SEMI_COLON);
        for (String param : paramsSplit) {
            String[] keyValue = param.split(EQUAL_SIGN);
            params.put(keyValue[0], keyValue[1]);
        }
    }

    public String getSession() {
        return params.get(JSESSION);
    }
}
