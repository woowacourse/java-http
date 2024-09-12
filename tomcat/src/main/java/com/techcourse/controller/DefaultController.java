package com.techcourse.controller;

import com.techcourse.controller.model.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

public class DefaultController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.isDefaultRequestPath();
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.sendError(HttpStatus.NOT_FOUND);
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.sendDefaultResponse();
    }
}
