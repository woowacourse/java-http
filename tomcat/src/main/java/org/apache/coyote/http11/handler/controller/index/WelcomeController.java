package org.apache.coyote.http11.handler.controller.index;

import org.apache.coyote.http11.handler.controller.base.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class WelcomeController extends AbstractController {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.ok("Hello world!");
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return super.doGet(httpRequest);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        return super.doPost(httpRequest);
    }
}
