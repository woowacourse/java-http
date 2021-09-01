package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.OK;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.Service;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    private final Service service;

    public RegisterController(Service service) {
        this.service = service;
    }

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        LOG.debug("Register - HTTP GET Request");

        response.forward(OK, request.getUri());
        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        LOG.debug("Register - HTTP POST Request");

        return service.doService(request, response);
    }
}
