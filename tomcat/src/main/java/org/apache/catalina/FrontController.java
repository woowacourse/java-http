package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Adaptor;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class FrontController implements Adaptor {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final FrontController frontController = new FrontController();
    private static ResourceHandler resourceHandler;

    private FrontController() {
    }

    public static FrontController getInstance() {
        return frontController;
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllers.getOrDefault(httpRequest.getRequestUrl(), resourceHandler);
    }

    public void addController(String url, Controller controller) {
        controllers.put(url, controller);
    }

    public void setResourceHandler(ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }
}
