package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

import static org.apache.coyote.http11.Method.GET;
import static org.apache.coyote.http11.Method.POST;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest) throws Exception {
        if (httpRequest.isMethod(GET)) {
            return doGet(httpRequest);
        } else if (httpRequest.isMethod(POST)) {
            return doPost(httpRequest);
        }

        return null;
    }

    protected abstract HttpResponse doPost(HttpRequest httpRequest);
    protected abstract HttpResponse doGet(HttpRequest httpRequest) throws IOException;
}
