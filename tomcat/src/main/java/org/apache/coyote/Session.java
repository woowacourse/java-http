package org.apache.coyote;

public interface Session {

    String getId();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);
}
