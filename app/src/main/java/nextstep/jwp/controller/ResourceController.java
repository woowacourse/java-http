package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.BAD_REQUEST;
import static nextstep.jwp.http.response.HttpStatus.OK;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("Resource - HTTP GET Request");

        response.forward(OK, request.getUri());
        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("Resource - HTTP POST Request");

        response.forward(BAD_REQUEST, BAD_REQUEST_PATH);
        return new View(BAD_REQUEST_PATH);
    }
}
