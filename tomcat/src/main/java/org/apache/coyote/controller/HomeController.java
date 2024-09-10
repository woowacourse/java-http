package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ViewResolver;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        ViewResolver.resolveView("index.html", response);
    }
}
