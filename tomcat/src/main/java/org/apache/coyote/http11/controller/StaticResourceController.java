package org.apache.coyote.http11.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController INSTANCE = new StaticResourceController();

    private StaticResourceController() {}

    public static StaticResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        serveStaticFile(request, response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        serveStaticFile(request, response);
    }
}
