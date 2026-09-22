package org.apache.catalina;

public record ResolvedSession(Session session, boolean isNewSession) {

    static ResolvedSession created(Session session) {
        return new ResolvedSession(session, true);
    }

    static ResolvedSession found(Session session) {
        return new ResolvedSession(session, false);
    }
}
