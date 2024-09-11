package org.apache.coyote.http11.request;

import java.util.UUID;
import org.apache.coyote.SessionIdGenerator;

public class UUIDSessionIdGenerator implements SessionIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
