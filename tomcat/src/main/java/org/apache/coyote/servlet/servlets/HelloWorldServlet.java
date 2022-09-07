package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.SessionFactory;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloWorldServlet extends AbstractServlet {

    public HelloWorldServlet(final SessionFactory sessionFactory) {
        super(sessionFactory);
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
        return HttpResponse.of(httpRequest, "/helloWorld.html", "200");
    }
}
