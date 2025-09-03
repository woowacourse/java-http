package org.apache.coyote.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandlerMapper {

    final List<AbstractHandler> handlers = new ArrayList<>();

    public HandlerMapper() {
        handlers.add(new HelloWorldHandler());
        handlers.add(new HtmlHandler());
        handlers.add(new JsHandler());
        handlers.add(new CssHandler());
    }

    public String getHandleResponse(final String requestTarget) throws IOException {
        final AbstractHandler handler = getHandler(requestTarget);
        if (handler == null) {
            return "HTTP/1.1 404 NOT FOUND ";
        }

        return handler.handle(requestTarget);
    }

    public AbstractHandler getHandler(final String requestTarget) {
        for (AbstractHandler handler : handlers) {
            if (handler.canHandler(requestTarget)) {
                return handler;
            }
        }

        return null;
    }
}
