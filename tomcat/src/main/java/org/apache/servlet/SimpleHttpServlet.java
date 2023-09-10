package org.apache.servlet;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.SimpleServletRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.SimpleServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleHttpServlet implements SimpleServlet {

    private static final Logger log = LoggerFactory.getLogger(SimpleHttpServlet.class);

    @Override
    public void service(
            SimpleServletRequest request, SimpleServletResponse response)
            throws ServletException, IOException {

        if (!(request instanceof HttpRequest && response instanceof HttpResponse)) {
            throw new ServletException();
        }

        HttpRequest req = (HttpRequest) request;
        HttpResponse resp = (HttpResponse) response;

        service(req, resp);
    }

    protected void service(HttpRequest request, HttpResponse response)
            throws ServletException, IOException {
        HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            doGet(request, response);

        } else if (httpMethod == HttpMethod.POST) {
            doPost(request, response);

        } else {
            log.error("http.method_not_implemented");
        }
    }

    protected void doGet(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        log.error("http.method_get_not_supported");
        throw new ServletException();
    }

    protected void doPost(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        log.error("http.method_post_not_supported");
        throw new ServletException();
    }
}
