package org.apache.catalina.controller;

import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        Request req = new Request(request);
        Response res = new Response();
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(req, res);
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(req, res);
        }
        response.setHttpResponseStartLine(res.getHttpResponseStartLine());
        response.setHttpResponseHeaders(res.getHttpResponseHeaders());
        response.setResponseBody(res.getResponseBody());
    }

    protected void doGet(final Request request, final Response response) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected void doPost(final Request request, final Response response) throws Exception {
        throw new UnsupportedOperationException();
    }
}
