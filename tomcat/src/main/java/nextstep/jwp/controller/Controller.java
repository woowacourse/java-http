package nextstep.jwp.controller;


import java.util.NoSuchElementException;

import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.request.mapping.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.service.Service;

public class Controller {

    private final Service service;

    public Controller(final Service service) {
        this.service = service;
    }

    @RequestMapping("/")
    public HttpResponse none() {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body("Hello world!");
    }

    @RequestMapping("/index")
    public HttpResponse index() {
        return success("index.html");
    }

    @RequestMapping("/login")
    public HttpResponse login(final Params params) {
        try {
            final String account = params.find("account");
            final String password = params.find("password");
            service.login(account, password);

            return redirect("/index.html");

        } catch (NoSuchElementException e) {
            return success("login.html");

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
