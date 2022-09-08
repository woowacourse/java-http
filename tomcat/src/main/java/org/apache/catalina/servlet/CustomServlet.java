package org.apache.catalina.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionListener;
import nextstep.jwp.service.UserService;
import org.apache.Servlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class CustomServlet implements Servlet {

    private final RequestMapping requestMapping;

    public CustomServlet() {
        ExceptionListener exceptionHandler = new ExceptionListener();
        final var userService = new UserService(new InMemoryUserRepository());
        this.requestMapping = RequestMapping.of(new HomeController(exceptionHandler),
                new LoginController(userService, exceptionHandler),
                new RegisterController(userService, exceptionHandler),
                new ResourceController(exceptionHandler));
    }

    public void service(HttpRequest request, HttpResponse response) {
        final var controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
