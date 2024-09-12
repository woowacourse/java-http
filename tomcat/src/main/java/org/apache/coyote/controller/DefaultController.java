package org.apache.coyote.controller;

import org.apache.coyote.component.HttpStatusCode;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.OK);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.OK);
    }
}
