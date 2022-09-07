package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterServlet extends Servlet {

    private static RegisterServlet registerServlet = new RegisterServlet();

    public RegisterServlet() {
    }

    @Override
    public Servlet init() {
        return registerServlet;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            return doGet(httpRequest);
        }
        return throwIfMethodDoesNotDefine(httpRequest, method);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest.getHttpVersion(), "/register.html", "200");
    }
}
