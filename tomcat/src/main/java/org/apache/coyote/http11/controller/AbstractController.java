package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }
    protected void doGet(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }
}
