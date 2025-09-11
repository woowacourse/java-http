package org.apache.catalina.servlet;

import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response);
}
