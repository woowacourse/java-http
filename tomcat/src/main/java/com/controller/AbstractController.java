package com.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public Http11Response control(final Http11Request request) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            return doPost(request);
        }

        return Http11Response.serverError();
    }

    protected Http11Response doGet(final Http11Request request) {
        return Http11Response.serverError();
    }

    protected Http11Response doPost(final Http11Request request) {
        return Http11Response.serverError();
    }
}
