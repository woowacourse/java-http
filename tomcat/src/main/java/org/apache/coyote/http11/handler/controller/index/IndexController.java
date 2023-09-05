package org.apache.coyote.http11.handler.controller.index;

import org.apache.coyote.http11.handler.controller.base.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doPost(request, response);
    }
}
