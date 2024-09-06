package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private static final Map<String, String> cookies = new ConcurrentHashMap<>();

    public static String getJsessionId(String account) {
        String uuid = UUID.randomUUID().toString();
        cookies.put(uuid, account);

        return uuid;
    }

    private HttpCookie() {}
}
