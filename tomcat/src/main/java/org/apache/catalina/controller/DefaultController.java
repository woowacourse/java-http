package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstrcatController {

    private static final DefaultController instance = new DefaultController();

    private DefaultController() {
        super("");
    }

    public static DefaultController getInstance() {
        return instance;
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
