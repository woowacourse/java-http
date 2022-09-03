package nextstep.jwp.servlet;

import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class HandlerMapper {

    private final HandlerMappings handlerMappings;
    private final ViewResolver viewResolver;

    public HandlerMapper(HandlerMappings handlerMappings,
                         ViewResolver viewResolver) {
        this.handlerMappings = handlerMappings;
        this.viewResolver = viewResolver;
    }

    public HttpResponse handle(HttpRequest request) {
        Handler handler = handlerMappings.getHandler(request);
        if (handler != null) {
            final var response = handler.handle(request);
            return (HttpResponse) response;
        }
        return viewResolver.findStaticResource(request.getUri());
    }
}
