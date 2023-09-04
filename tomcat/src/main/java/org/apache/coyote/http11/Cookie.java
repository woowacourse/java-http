package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Cookie {

    private final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> value = new HashMap<>();

    private Cookie() {
    }

    public Cookie(final String cookie) {
        if (cookie == null) {
            return;
        }
        this.value.putAll(Arrays.stream(cookie.split("; "))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(
                        it -> it[0], it -> it[1]
                )));
    }

    public static Cookie createCookieContainJsessionId() {
        final Cookie cookie = new Cookie();
        cookie.value.put("JSESSIONID", String.valueOf(UUID.randomUUID()));
        return cookie;
    }

    public boolean containsJsessionId() {
        return value.containsKey(JSESSIONID);
    }

    public Map<String, String> getValue() {
        return value;
    }
}
