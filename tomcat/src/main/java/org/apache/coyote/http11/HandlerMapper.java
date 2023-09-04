package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.handler.BadRequestHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.RootHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapper {

    private static final List<Handler> HANDLERS = List.of(new RootHandler(),
            new StaticResourceHandler(),
            new LoginHandler(), new RegisterHandler());
    private static final Handler HANDLER_NONE_MATCHED_DEFAULT = new BadRequestHandler();

    public Handler find(HttpRequest httpRequest) {
        for (Handler handler : HANDLERS) {
            if (handler.supports(httpRequest)) {
                return handler;
            }
        }
        return HANDLER_NONE_MATCHED_DEFAULT;
    }
}
