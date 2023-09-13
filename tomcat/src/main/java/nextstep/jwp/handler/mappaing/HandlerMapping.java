package nextstep.jwp.handler.mappaing;

import java.util.Map;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.request.HttpRequest;

public class HandlerMapping {

    private final Map<String, Controller> handler;

    public HandlerMapping(Map<String, Controller> handler) {
        this.handler = handler;
    }

    public Controller getHandler(HttpRequest request) {
        String uri = request.requestLine().uri().uri();
        Controller controller = handler.get(uri);
        if (controller == null) {
            return handler.get("/**");
        }
        return controller;
    }
}
