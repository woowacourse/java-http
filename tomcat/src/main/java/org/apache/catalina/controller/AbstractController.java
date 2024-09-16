package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        final String method = request.getRequestLine().getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(final Request request, final Response response) throws Exception {}

    protected void doGet(final Request request, final Response response) throws Exception {}
}
