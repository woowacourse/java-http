package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final Map<String, String> cookies = new HashMap<>();

    public static String getJsessionId(String account) {
        String uuid = UUID.randomUUID().toString();
        cookies.put(uuid, account);

        return uuid;
    }

    private HttpCookie() {}
}
