package nextstep.jwp.handler.mappaing;

import java.util.List;
import javax.annotation.Nullable;
import nextstep.jwp.handler.RequestHandler;
import org.apache.catalina.servlet.request.HttpRequest;

public class HandlerMapping {

    private final List<RequestHandler> handler;

    public HandlerMapping(List<RequestHandler> handler) {
        this.handler = handler;
    }

    public @Nullable RequestHandler getHandler(HttpRequest request) {
        for (RequestHandler requestHandler : handler) {
            if (requestHandler.canHandle(request)) {
                return requestHandler;
            }
        }
        return null;
    }
}
