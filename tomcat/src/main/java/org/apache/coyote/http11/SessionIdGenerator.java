package org.apache.coyote.http11;

import java.util.UUID;

public class SessionIdGenerator {

    public SessionIdGenerator() {

    }

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
