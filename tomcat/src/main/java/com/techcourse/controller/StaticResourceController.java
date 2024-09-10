package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            Path path = request.getPath();
            generateStaticResponse(path.getUri(), HttpStatus.OK, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }
}
