package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloWorldServlet extends Servlet {

    private static HelloWorldServlet helloWorldServlet = new HelloWorldServlet();

    public HelloWorldServlet() {
    }

    @Override
    public Servlet init() {
        return helloWorldServlet;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            return doGet(httpRequest);
        }
        return createNotFoundResponse(httpRequest);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest.getHttpVersion(), "/helloWorld.html", "200");
    }
}
