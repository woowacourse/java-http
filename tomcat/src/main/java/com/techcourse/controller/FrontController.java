package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private static FrontController instance = new FrontController();

    private final RequestMapping requestMapping;

    private FrontController() {
        requestMapping = new RequestMapping();
    }

    public static FrontController getInstance() {
        return instance;
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (UncheckedServletException e) {
            ControllerAdviser.service(e, request, response);
        }
    }
}
