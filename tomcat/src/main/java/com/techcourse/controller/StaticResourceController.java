package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

import java.io.IOException;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        try {
            Path path = request.getPath();
            return generateResponse(STATIC_RESOURCE_LOCATION + path.getUri(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }
}
