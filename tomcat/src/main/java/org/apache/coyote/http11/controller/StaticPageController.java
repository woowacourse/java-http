package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resolver.View;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticPageController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        View.renderStaticPage(request.getUri(), response);
    }
}
