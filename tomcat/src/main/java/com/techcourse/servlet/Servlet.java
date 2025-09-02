package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public interface Servlet {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
