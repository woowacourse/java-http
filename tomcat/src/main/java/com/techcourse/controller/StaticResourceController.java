package com.techcourse.controller;

import com.techcourse.controller.model.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;
import org.apache.coyote.util.ResourceFinder;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        try {
            ResourceFinder.findBy(httpRequest.getResourcePath());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.sendError(HttpStatus.NOT_FOUND);
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.sendStaticResourceResponse(httpRequest, HttpStatus.OK);
    }
}
