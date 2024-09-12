package org.apache.catalina.handler;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http.request.HttpRequest;

public class RequestMapping {

    public Map<String, Controller> mappings = new HashMap<>();

    public RequestMapping(Map<String, Controller> mappings) {
        mappings.put("/login", new LoginController());
    }

    public Controller getController(final HttpRequest request) {
        String requestURI = request.getRequestURI();
        return mappings.getOrDefault(requestURI, new NotFoundController());
    }
}
