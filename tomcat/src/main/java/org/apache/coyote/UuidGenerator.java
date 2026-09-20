package org.apache.coyote;

import java.util.UUID;

public class UuidGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
