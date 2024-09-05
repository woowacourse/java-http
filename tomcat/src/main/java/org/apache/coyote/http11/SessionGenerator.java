package org.apache.coyote.http11;

import java.util.UUID;

public class SessionGenerator {

    public static String generate() {
        return "JSESSIONID=" + UUID.randomUUID();
    }
}
