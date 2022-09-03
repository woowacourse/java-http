package nextstep.jwp.servlet;

import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class HandlerMapper {

    private final HandlerMappings handlerMappings;
    private final ResourceController resourceController;

    public HandlerMapper(HandlerMappings handlerMappings,
                         ResourceController resourceController) {
        this.handlerMappings = handlerMappings;
        this.resourceController = resourceController;
    }

    public HttpResponse handle(HttpRequest request) {
        Handler handler = handlerMappings.getHandler(request);
        if (handler != null) {
            final var response = handler.handle(request);
            return (HttpResponse) response;
        }
        return resourceController.find(request);
    }
}
