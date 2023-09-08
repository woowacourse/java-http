package org.apache.controller;

import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;

import static nextstep.jwp.common.StatusCode.NOT_FOUND;

public class NotFoundController extends AbstractController{
    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(NOT_FOUND);
    }
}
