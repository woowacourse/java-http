package org.apache.catalina.session;

public interface Session {

    String getId();

    Object getAttribute(final String name);

    void setAttribute(final String name, final Object value);

    void removeAttribute(final String name);

    void invalidate();
}
