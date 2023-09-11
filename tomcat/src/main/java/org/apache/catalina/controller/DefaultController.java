package org.apache.catalina.controller;

import org.apache.coyote.http11.exception.HostingFileNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;

public class DefaultController extends AbstrcatController {

    private static final DefaultController instance = new DefaultController();
    private static final String NOT_FOUND_PAGE = "/404.html";

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
        try {
            response.hostingPage(request.getPath());
        } catch (HostingFileNotFoundException e) {
            response.hostingPage(NOT_FOUND_PAGE);
            response.setStatus(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.methodNotAllowed();
    }
}
