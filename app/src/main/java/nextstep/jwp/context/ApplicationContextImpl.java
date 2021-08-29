package nextstep.jwp.context;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.dispatcher.handler.Handler;

public class ApplicationContextImpl implements ApplicationContext {

    private final Map<String, Handler> handlers;
    private final Map<String, Object> attributes;

    public ApplicationContextImpl() {
        handlers = new HashMap<>();
        attributes = new HashMap<>();
    }

    public ApplicationContextImpl(
        Map<String, Handler> handlers,
        Map<String, Object> attributes
    ) {
        this.handlers = handlers;
        this.attributes = attributes;
    }

    @Override
    public Handler getHandler(String url) {
        return handlers.get(url);
    }

    @Override
    public void addHandler(String url, Handler handler) {
        this.handlers.putIfAbsent(url, handler);
    }

    @Override
    public void addHandlers(Map<String, Handler> handlers) {
        this.handlers.putAll(handlers);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.putIfAbsent(name, value);
    }
}
