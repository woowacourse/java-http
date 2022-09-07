package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Servlet {

    private static Servlet servlet;

    public abstract Servlet init();

    public abstract HttpResponse service(final HttpRequest httpRequest);

    void destroy() {
        servlet = null;
    }

    protected HttpResponse throwIfMethodDoesNotDefine(final HttpRequest httpRequest, final Method method) {
        throw new IllegalArgumentException(
            String.format("정의되지 않은 method 입니다. [%s, %s]", httpRequest.getUrl(), method)
        );
    }
}
