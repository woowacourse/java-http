package nextstep.jwp.controller;


import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.service.Service;

public class Controller {

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
        final String account = params.find("account");
        final String password = params.find("password");
        service.login(account, password);

        return asResponse(HttpStatus.OK, "login.html");
    }

    private HttpResponse asResponse(final HttpStatus status, final String filePath) {
        return new HttpResponse()
                .status(status)
                .body(new Resource(filePath));
    }
}
