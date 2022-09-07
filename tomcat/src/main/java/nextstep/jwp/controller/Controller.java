package nextstep.jwp.controller;


import java.util.NoSuchElementException;

import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.request.annotation.RequestMapping;
import org.apache.coyote.http11.request.annotation.RequestParam;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Controller {

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
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(password)) {
            return redirect("/401.html");
        }

        return redirect("/index.html");
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
