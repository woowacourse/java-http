package nextstep.jwp.controller;


import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.request.mapping.RequestMapping;
import org.apache.coyote.http11.request.mapping.RequestParam;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.service.Service;

public class Controller {

    private final Service service;

    public Controller(final Service service) {
        this.service = service;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpResponse none() {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body("Hello world!");
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public HttpResponse index() {
        return success("index.html");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public HttpResponse login() {
        return success("login.html");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public HttpResponse login(@RequestParam("account") final String account,
                              @RequestParam("password") final String password) {
        try {
            service.login(account, password);
            return redirect("/index.html");

        } catch (IllegalArgumentException e) {
            return redirect("/401.html");
        }
    }

    private HttpResponse success(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath));
    }

    private HttpResponse redirect(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.FOUND)
                .location(filePath);
    }
}
