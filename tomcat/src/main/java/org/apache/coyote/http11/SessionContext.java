package org.apache.coyote.http11;

public record SessionContext(
    Session session,
    boolean created
) {

    public static SessionContext created(final Session session) {
        return new SessionContext(session, true);
    }
    public static SessionContext notCreated(final Session session) {
        return new SessionContext(session, false);
    }

}
