package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        response.forward(path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException();
    }


}
