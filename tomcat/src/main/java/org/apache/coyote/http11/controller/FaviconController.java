package org.apache.coyote.http11.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FaviconController extends AbstractController {

    private static final FaviconController instance = new FaviconController();

    private FaviconController() {}

    public static FaviconController getInstance() {
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.addStatusLine("HTTP/1.1 204 No Content");
        response.addHeader("Content-Length", "0");
        response.writeResponse();
    }
}
