package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.SessionFactory;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractServlet {

    protected final SessionFactory sessionFactory;

    protected AbstractServlet(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public abstract HttpResponse service(final HttpRequest httpRequest);

    protected HttpResponse createNotFoundResponse(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, "/404.html", "404");
    }
}
