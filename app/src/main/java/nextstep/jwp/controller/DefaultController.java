package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        serveResource(request, response);
    }

    protected void serveResource(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();
        response.forward(uri);
    }
}
