package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.BasicController;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.LoginController;

public class RequestMapping {

    private final Map<String, Controller> requestMap;

    private RequestMapping(final Map<String, Controller> requestMap) {
        this.requestMap = requestMap;
    }

    public static RequestMapping init() {
        Map<String, Controller> requestMap = new HashMap<>();
        requestMap.put("/", new HomeController());
        requestMap.put("/index.html", new BasicController());
        requestMap.put("/login", new LoginController());
        return new RequestMapping(requestMap);
    }

    public Controller find(final String requestUri) {
        Controller controller = requestMap.get(requestUri);
        if (controller == null) {
            return new BasicController();
        }
        return controller;
    }
}
