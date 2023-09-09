package org.apache.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public abstract class AbstractController implements Controller {

    protected static final String CONTENT_LENGTH = "Content-Length";
    protected static final String CONTENT_TYPE = "Content-Type";

    protected static final String INDEX_PATH = "/index.html";
    protected static final String UNAUTHORIZED_PATH = "/401.html";

    @Override
    public void service(Request request, Response response) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new ControllerException(request.getHttpMethod());
    }

    protected void doGet(Request request, Response response) {
        throw new ControllerException(HttpMethod.GET);
    }

    protected void doPost(Request request, Response response) {
        throw new ControllerException(HttpMethod.POST);
    }
}
