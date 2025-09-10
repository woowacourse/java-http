package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final String method = request.getStartLine().getHttpMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
            return;
        }
        if ("POST".equals(method)) {
            doPost(request, response);
            return;
        }
        response.setStatus(404, "Not Found");
        response.writeText("No Route", "text/html;charset=utf-8");
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {

    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {

    }
}
