package org.apache.coyote.http11.request;

import org.apache.catalina.Session;

public record SessionResult(
        Session session,
        boolean newSession
) {
}
