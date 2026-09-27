package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws IOException {
        return doGet(httpRequest);
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = HttpResponse.empty(httpRequest);
        return httpResponse.ok();
    }
}
