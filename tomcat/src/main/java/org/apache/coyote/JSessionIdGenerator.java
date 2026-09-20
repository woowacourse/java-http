package org.apache.coyote;

import java.util.UUID;

public class JSessionIdGenerator {

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
