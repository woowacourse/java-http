package org.apache.catalina.servlet;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface HttpServlet {

    void service(HttpRequest request, HttpResponse response);
}
