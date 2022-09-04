package nextstep.jwp.servlet;

import nextstep.jwp.servlet.handler.Handler;
import nextstep.jwp.servlet.handler.HandlerMappings;
import org.apache.coyote.servlet.request.HttpRequest;

public class HandlerMapper {

    private final HandlerMappings handlerMappings;

    public HandlerMapper(HandlerMappings handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public boolean hasMappedHandler(HttpRequest request) {
        Handler handler = handlerMappings.findHandler(request);
        return handler != null;
    }

    public Handler getMappedHandler(HttpRequest request) {
        return handlerMappings.findHandler(request);
    }
}
