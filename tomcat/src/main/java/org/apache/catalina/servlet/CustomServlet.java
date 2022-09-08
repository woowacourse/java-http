package org.apache.catalina.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionListener;
import nextstep.jwp.service.UserService;
import org.apache.Servlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.support.HttpException;

public class CustomServlet implements Servlet {

    private final RequestMapping requestMapping;
    private final ExceptionHandler exceptionHandler;

    public CustomServlet() {
        final var userService = new UserService(new InMemoryUserRepository());
        this.requestMapping = RequestMapping.of(new HomeController(),
                new LoginController(userService), new RegisterController(userService));
        this.exceptionHandler = new ExceptionListener();
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            handleRequest(request, response);
        } catch (HttpException exception) {
            exceptionHandler.handle(exception, response);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        if (!requestMapping.hasMappedController(request)) {
            response.ok().setViewResource(request.getUri());
            return;
        }
        final var controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
