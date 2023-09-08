package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (POST == request.httpMethod()) {
            doPost(request, response);
        }
        if (GET == request.httpMethod()) {
            doGet(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {

    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {

    }
}
