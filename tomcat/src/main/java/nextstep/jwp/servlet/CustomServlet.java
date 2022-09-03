package nextstep.jwp.servlet;

import nextstep.jwp.controller.GetController;
import nextstep.jwp.controller.PostController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.service.UserService;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class CustomServlet implements Servlet {

    private final HandlerMapper handlerMapper;
    private final ExceptionHandler exceptionHandler;
    private final ViewResolver viewResolver;

    public CustomServlet() {
        final var viewResolver = new ViewResolver();
        final var handlerMappings = HandlerMappings.of(new GetController(),
                new PostController(new UserService(new InMemoryUserRepository())));
        this.handlerMapper = new HandlerMapper(handlerMappings, viewResolver);
        this.exceptionHandler = new ExceptionHandler();
        this.viewResolver = viewResolver;
    }

    public HttpResponse service(HttpRequest request) {
        try {
            return handlerMapper.handle(request);
        } catch (HttpException exception) {
            final var viewResource = exceptionHandler.handle(exception);
            return viewResolver.findStaticResource(viewResource);
        }
    }
}
