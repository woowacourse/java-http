package nextstep.jwp.controller;


import java.util.NoSuchElementException;

import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.service.Service;

public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final Service service;

    public Controller(final Service service) {
        this.service = service;
    }

    public HttpResponse none() {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body("Hello world!");
    }

    public HttpResponse index() {
        return asResponse(HttpStatus.OK, "index.html");
    }

    public HttpResponse login(final Params params) {
        try {
            final String account = params.find("account");
            final String password = params.find("password");
            service.login(account, password);
        } catch (NoSuchElementException e) {
            LOG.error(e.getMessage());
        }

        return asResponse(HttpStatus.OK, "login.html");
    }

    private HttpResponse asResponse(final HttpStatus status, final String filePath) {
        return new HttpResponse()
                .status(status)
                .body(new Resource(filePath));
    }
}
