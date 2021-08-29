package nextstep.jwp.controller;

import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;

import java.io.IOException;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getPath());
    }
}
