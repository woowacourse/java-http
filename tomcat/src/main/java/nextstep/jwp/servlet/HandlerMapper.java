package nextstep.jwp.servlet;

import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.servlet.handler.Handler;
import nextstep.jwp.servlet.handler.HandlerMappings;
import nextstep.jwp.servlet.view.ViewResolver;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.ResponseEntity;
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

    public ResponseEntity handle(HttpRequest request) {
        Handler handler = handlerMappings.getHandler(request);
        if (handler == null) {
            return viewResolver.findStaticResource(request.getUri());
        }
        return handle(request, handler);
    }

    private ResponseEntity handle(HttpRequest request, Handler handler) {
        final var response = handler.handle(request);
        if (handler.hasReturnTypeOf(String.class)) {
            return viewResolver.findStaticResource((String) response);
        }
        ResponseEntity responseEntity = (ResponseEntity) response;
        String viewResource = responseEntity.getViewResource();
        if (viewResource == null) {
            return responseEntity;
        }
        return viewResolver.findStaticResource(viewResource);
    }

    public ResponseEntity handle(HttpException exception) {
        final var responseEntity = exceptionHandler.handle(exception);
        return viewResolver.findStaticResource(responseEntity);
    }
}
