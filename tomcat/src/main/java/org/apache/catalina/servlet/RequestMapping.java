package org.apache.catalina.servlet;

import org.apache.coyote.http.request.HttpRequest;

public interface RequestMapping {

    Servlet getServlet(HttpRequest httpRequest);
}
