package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws Exception {
        return doGet(httpRequest);
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = HttpResponse.of(httpRequest);
        return httpResponse.ok();
    }
}
