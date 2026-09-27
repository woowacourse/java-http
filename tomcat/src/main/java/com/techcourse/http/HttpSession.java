package com.techcourse.http;

public interface HttpSession {

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void invalidate();
}
