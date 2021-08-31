package nextstep.jwp.context;

import java.util.Map;
import nextstep.jwp.dispatcher.handler.Handler;

public interface ApplicationContext {

    Handler getHandler(String url);
    void addHandler(String url, Handler handler);
    void addHandlers(Map<String, Handler> handlers);
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
}
