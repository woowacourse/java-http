package org.apache.coyote.http11.auth;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
