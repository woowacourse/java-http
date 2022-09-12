package org.apache.coyote.http11.session;

import java.util.UUID;

public class JSessionId {
    private final String randomId;

    private JSessionId() {
        this.randomId = UUID.randomUUID().toString();
    }

    public static String create() {
        return new JSessionId().randomId;
    }
}
