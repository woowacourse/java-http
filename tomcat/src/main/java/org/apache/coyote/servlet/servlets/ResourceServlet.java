package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceServlet extends Servlet {

    private static ResourceServlet resourceServlet = new ResourceServlet();

    public ResourceServlet() {
    }

    @Override
    public Servlet init() {
        return resourceServlet;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            return doGet(httpRequest);
        }
        throw new IllegalArgumentException(
            String.format("정의되지 않은 method 입니다. [%s, %s]", httpRequest.getUrl(), method)
        );
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest.getHttpVersion(), httpRequest.getUrl(), "OK");
    }
}
