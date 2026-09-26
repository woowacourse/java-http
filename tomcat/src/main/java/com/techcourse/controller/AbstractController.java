package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            if ("GET".equals(request.getMethod())) {
                doGet(request, response);
                return;
            }
            if ("POST".equals(request.getMethod())) {
                doPost(request, response);
                return;
            }
            response.sendMethodNotAllowed();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            response.sendInternalServerError();
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
