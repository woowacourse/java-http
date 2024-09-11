package org.apache.coyote.util;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
