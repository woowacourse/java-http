package org.apache.coyote.http11.request;

import java.util.UUID;

public class HttpCookie {

    public String getCookieSession() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
