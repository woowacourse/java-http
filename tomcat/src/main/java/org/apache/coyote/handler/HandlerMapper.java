package org.apache.coyote.handler;

import java.util.ArrayList;
import java.util.List;

public class HandlerMapper {

    private static class HandlerMapperHolder {
        private static final HandlerMapper INSTANCE = new HandlerMapper();
    }
    private final List<AbstractHandler> handlers = new ArrayList<>();

    public static HandlerMapper getInstance() {
        return HandlerMapperHolder.INSTANCE;
    }

    private HandlerMapper() {
        handlers.add(new HelloWorldHandler());
        handlers.add(new LoginHandler());
        handlers.add(new HtmlHandler());
        handlers.add(new JsHandler());
        handlers.add(new CssHandler());
    }

    public AbstractHandler getHandler(final String requestTarget) {
        for (AbstractHandler handler : handlers) {
            if (handler.canHandle(requestTarget)) {
                return handler;
            }
        }

        return null;
    }
}
