package nextstep.jwp.servlet;

import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class HandlerMapper {

    private final HandlerMappings handlerMappings;
    private final ResourceRegistry resourceRegistry;

    public HandlerMapper(HandlerMappings handlerMappings,
                         ResourceRegistry resourceRegistry) {
        this.handlerMappings = handlerMappings;
        this.resourceRegistry = resourceRegistry;
    }

    public HttpResponse handle(HttpRequest request) {
        Handler handler = handlerMappings.getHandler(request);
        if (handler != null) {
            final var response = handler.handle(request);
            return (HttpResponse) response;
        }
        return resourceRegistry.findStaticResource(request.getUri());
    }
}
