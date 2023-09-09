package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String JSESSION = "JSESSIONID";
    private static final String SEMI_COLON = "; ";
    private static final String EQUAL_SIGN = "=";

    private final Map<String, String> params = new HashMap<>();

    public Cookie(String cookies) {
        String[] paramsSplit = cookies.split(SEMI_COLON);
        for (String param : paramsSplit) {
            String[] keyValue = param.split(EQUAL_SIGN);
            params.put(keyValue[0], keyValue[1]);
        }
    }

    public boolean hasSessionId() {
        return params.containsKey(JSESSION);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
