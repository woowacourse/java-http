package nextstep.jwp.web.ui;

import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.server.http.response.HttpResponse;

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
