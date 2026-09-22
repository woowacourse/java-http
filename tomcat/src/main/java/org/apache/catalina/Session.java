package org.apache.catalina;

public interface Session {

    String getId();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    void invalidate();
}
