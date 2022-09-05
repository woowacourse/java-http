package nextstep.jwp.servlet;

import nextstep.jwp.controller.AuthController;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.HandlerExecutor;
import nextstep.jwp.servlet.handler.HandlerMappings;
import nextstep.jwp.servlet.view.ViewResolver;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.response.ResponseEntity;
import org.apache.coyote.support.HttpException;

public class CustomServlet implements Servlet {

    private final HandlerMapper handlerMapper;
    private final HandlerExecutor handlerExecutor;
    private final ViewResolver viewResolver;

    public CustomServlet() {
        final var handlerMappings = HandlerMappings.of(new HomeController(),
                new AuthController(new UserService(new InMemoryUserRepository())));
        final var viewResolver = new ViewResolver();
        this.handlerMapper = new HandlerMapper(handlerMappings);
        this.handlerExecutor = new HandlerExecutor(new ExceptionHandler(), viewResolver);
        this.viewResolver = viewResolver;
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            response.update(handleOrResolveView(request));
        } catch (HttpException exception) {
            response.update(handlerExecutor.handle(exception));
        }
    }

    private ResponseEntity handleOrResolveView(HttpRequest request) {
        if (!handlerMapper.hasMappedHandler(request)) {
            return viewResolver.findStaticResource(request.getUri());
        }
        final var handler = handlerMapper.getMappedHandler(request);
        return handlerExecutor.handle(request, handler);
    }
}
