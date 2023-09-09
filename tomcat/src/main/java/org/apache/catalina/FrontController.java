package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Adaptor;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class FrontController implements Adaptor {

    private final Map<String, Controller> controllers = new HashMap<>();
    private ResourceHandler resourceHandler;

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
