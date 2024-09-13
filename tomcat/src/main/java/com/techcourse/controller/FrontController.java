package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class FrontController implements Controller {

    private static final FrontController instance = new FrontController();

    private final RequestMapping requestMapping;

    private FrontController() {
        this.requestMapping = RequestMapping.getInstance();
    }

    public static FrontController getInstance() {
        return instance;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
