package org.apache.catalina.session;

import java.util.UUID;

public class SessionIdGenerator {

    private SessionIdGenerator() {}

    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
