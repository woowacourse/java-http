package org.apache.coyote.http11.auth;

import java.util.UUID;

public class SessionGenerator {

    public Session generate() {
        return new Session(UUID.randomUUID().toString());
    }
}
