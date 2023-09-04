package org.apache.coyote.http11.session;

import java.util.UUID;

public class JSessionIdGenerator {

    private JSessionIdGenerator() {
    }

    public static String generateRandomSessionId() {
        return UUID.randomUUID().toString();
    }
}
