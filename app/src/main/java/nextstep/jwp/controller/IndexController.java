package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.type.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Index Request: {}", request.getPath());
        response.setStatusLine(StatusCode.OK);
        response.responseMessage("Hello world!");
    }
}
