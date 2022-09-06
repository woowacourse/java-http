package org.apache.coyote.util;

import java.util.UUID;

public class Cookies {

    public static String ofJSessionId() {
        return UUID.randomUUID().toString();
    }
}
