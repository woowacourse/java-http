package org.apache.coyote.http11.common;

import java.util.UUID;

public class Cookie {

    public static UUID generateCookie() {
        return UUID.randomUUID();
    }
}
