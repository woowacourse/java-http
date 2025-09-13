package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }

        if (request.isPost()) {
            doPost(request, response);
            return;
        }

        response.sendError(Http11Status.METHOD_NOT_ALLOW);
    }

    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        response.sendError(Http11Status.METHOD_NOT_ALLOW);
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        response.sendError(Http11Status.METHOD_NOT_ALLOW);
    }
}
