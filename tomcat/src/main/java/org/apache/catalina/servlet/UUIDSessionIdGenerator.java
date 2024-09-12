package org.apache.catalina.servlet;

import java.util.UUID;
import org.apache.catalina.session.SessionIdGenerator;

public class UUIDSessionIdGenerator implements SessionIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
