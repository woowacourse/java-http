package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.component.HttpStatusCode;

public class DefaultController extends AbstractController implements Controller {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.OK);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.OK);
    }
}
