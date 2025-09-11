package org.apache.catalina;

import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletMapper;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;

public class ProcessBroker {

    private final ServletMapper servletMapper;

    public ProcessBroker(final ServletMapper servletMapper) {
        this.servletMapper = servletMapper;
    }

    public void brokeRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        String requestPath = httpRequest.getRequestPath();
        Servlet servlet = servletMapper.mappingServlet(requestPath);
        servlet.service(httpRequest, httpResponse);
    }
}
