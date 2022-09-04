package nextstep.jwp.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.AuthController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.HandlerMappings;
import nextstep.jwp.servlet.view.ViewResolver;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.servlet.session.SessionRepository;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class CustomServlet implements Servlet {

    private final HandlerMapper handlerMapper;

    public CustomServlet() {
        final var handlerMappings = HandlerMappings.of(new HomeController(),
                new AuthController(new UserService(new InMemoryUserRepository()), new SessionRepository()));
        this.handlerMapper = new HandlerMapper(handlerMappings, new ExceptionHandler(), new ViewResolver());
    }

    public HttpResponse service(HttpRequest request) {
        try {
            return handlerMapper.handle(request);
        } catch (HttpException exception) {
            return handlerMapper.handle(exception);
        }
    }
}
