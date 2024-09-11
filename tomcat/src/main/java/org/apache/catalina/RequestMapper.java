package org.apache.catalina;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.ResourceController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper {

    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/login", new LoginController());
    }

    private RequestMapper() {
    }

    public static Controller getController(HttpRequest request) {
        if (request.isStaticResource()) {
            return new ResourceController();
        }

        return controllerMap.get(request.getUri());
    }
}
