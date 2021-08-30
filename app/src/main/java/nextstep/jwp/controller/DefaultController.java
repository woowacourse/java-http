package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Resource Request: {}", request.getPath());
        response.responseOk(request.getPath());
    }
}
