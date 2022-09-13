package org.apache.coyote.controller;

import static org.apache.coyote.request.startline.HttpMethod.GET;
import static org.apache.coyote.request.startline.HttpMethod.POST;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.getRequestMethod();

        if (method.equals(POST)) {
            doPost(request, response);
        }
        if (method.equals(GET)) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }
    protected void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
}
