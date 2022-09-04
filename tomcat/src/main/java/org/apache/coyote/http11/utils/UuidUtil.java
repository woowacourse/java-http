package org.apache.coyote.http11.utils;

import java.util.UUID;

public class UuidUtil {

    public static String randomUuidString() {
        return UUID.randomUUID()
                .toString();
    }
}
