package org.apache.catalina.servletContainer;

import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ServletContainer {

    private final ServletMapper servletMapper;

    public ServletContainer() {
        this.servletMapper = new ServletMapper();
    }

    public HttpResponse process(final HttpRequest httpRequest) {
        Servlet servlet = servletMapper.findServlet(httpRequest);
        return servlet.service(httpRequest);
    }
}
