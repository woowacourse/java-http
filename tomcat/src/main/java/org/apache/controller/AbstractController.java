package org.apache.controller;

import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import static nextstep.jwp.common.StatusCode.NOT_FOUND;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        if (httpRequest.getHttpLine().getHttpMethod().equals(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
        if (httpRequest.getHttpLine().getHttpMethod().equals(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse){
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(NOT_FOUND);
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException;
}
