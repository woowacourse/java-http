package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;

public interface Servlet {

    boolean canHandle(HttpRequest request);

    String handle(HttpRequest request);
}
