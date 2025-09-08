package org.apache.catalina.servletContainer;

import java.util.Optional;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class ServletContainer {

    private final ServletMapper servletMapper;

    public ServletContainer() {
        this.servletMapper = new ServletMapper();
    }

    public void process(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Optional<Servlet> servlet = servletMapper.findServlet(httpRequest);
        if (servlet.isEmpty()) {
            httpResponse.init("", ContentType.HTML, HttpStatus.NOT_FOUND);
            return;
        }

        servlet.get().service(httpRequest, httpResponse);
    }
}
