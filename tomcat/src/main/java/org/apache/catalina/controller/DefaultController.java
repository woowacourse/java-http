package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstrcatController {

    private static final DefaultController instance = new DefaultController();

    public static DefaultController getInstance() {
        return instance;
    }

    private DefaultController() {
    }

    @Override
    public boolean isMappedController(HttpRequest request) {
        return false;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.hostingPage(request.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.methodNotAllowed();
    }
}
