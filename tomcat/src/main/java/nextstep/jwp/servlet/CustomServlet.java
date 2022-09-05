package nextstep.jwp.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionListener;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.ExceptionHandler;
import nextstep.jwp.servlet.handler.HandlerMapping;
import nextstep.jwp.servlet.view.ViewResolver;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpException;

public class CustomServlet implements Servlet {

    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;
    private final ExceptionHandler exceptionHandler;

    public CustomServlet() {
        final var userService = new UserService(new InMemoryUserRepository());
        final var handlerMapping = HandlerMapping.of(new HomeController(),
                new LoginController(userService), new RegisterController(userService));
        this.handlerMapping = handlerMapping;
        this.viewResolver = new ViewResolver();
        this.exceptionHandler = new ExceptionListener();
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            handleRequest(request, response);
            viewResolver.resolve(response);
        } catch (HttpException exception) {
            exceptionHandler.handle(exception, response);
            viewResolver.resolve(response);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        if (!handlerMapping.hasMappedHandler(request)) {
            response.ok().setViewResource(request.getUri());
            return;
        }
        final var handler = handlerMapping.getMappedHandler(request);
        handler.service(request, response);
    }
}
