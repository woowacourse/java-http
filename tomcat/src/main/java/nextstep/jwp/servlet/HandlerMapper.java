package nextstep.jwp.servlet;

import nextstep.jwp.exception.ExceptionHandler;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpException;

public class HandlerMapper {

    private final HandlerMappings handlerMappings;
    private final ExceptionHandler exceptionHandler;
    private final ViewResolver viewResolver;

    public HandlerMapper(HandlerMappings handlerMappings,
                         ExceptionHandler exceptionHandler,
                         ViewResolver viewResolver) {
        this.handlerMappings = handlerMappings;
        this.exceptionHandler = exceptionHandler;
        this.viewResolver = viewResolver;
    }

    public HttpResponse handle(HttpRequest request) {
        Handler handler = handlerMappings.getHandler(request);
        if (handler == null) {
            return viewResolver.findStaticResource(request.getUri());
        }
        return handle(request, handler);
    }

    private HttpResponse handle(HttpRequest request, Handler handler) {
        final var response = handler.handle(request);
        if (handler.hasReturnTypeOf(HttpResponse.class)) {
            return (HttpResponse) response;
        }
        if (handler.hasReturnTypeOf(String.class)) {
            return viewResolver.findStaticResource((String) response);
        }
        throw new UnsupportedOperationException("invalid request");
    }

    public HttpResponse handle(HttpException exception) {
        final var viewResource = exceptionHandler.handle(exception);
        return viewResolver.findStaticResource(viewResource);
    }
}
