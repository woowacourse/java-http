package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cookie {

    private Map<String, String> cookies = new HashMap<>();

    public static UUID generateCookie() {
        return UUID.randomUUID();
    }
}
