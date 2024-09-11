package org.apache.coyote.http11;

import java.util.UUID;

public class SessionGenerator {
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
