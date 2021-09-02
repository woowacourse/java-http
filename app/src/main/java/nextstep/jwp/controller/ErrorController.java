package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.NOT_FOUND;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);
    private static final String NOT_FOUND_PATH = "/404";

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("Error - HTTP GET Request");
        return getView(response);
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("Error - HTTP POST Request");
        return getView(response);
    }

    private View getView(HttpResponse response) {
        response.forward(NOT_FOUND, NOT_FOUND_PATH);
        return new View(NOT_FOUND_PATH);
    }
}
