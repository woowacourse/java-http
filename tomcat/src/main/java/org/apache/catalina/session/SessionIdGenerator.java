package org.apache.catalina.session;

import java.util.UUID;

public class SessionIdGenerator {

    public SessionIdGenerator() {

    }

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
