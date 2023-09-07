package org.apache.controller;

import org.apache.common.HttpMethod;
import org.apache.common.HttpRequest;
import org.apache.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

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

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException;
}
