package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class StaticPageController extends AbstractController {

    private static final StaticPageController instance = new StaticPageController();

    private StaticPageController() {
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        setResponseContent(request, response);
    }

    public static StaticPageController getInstance() {
        return instance;
    }
}
