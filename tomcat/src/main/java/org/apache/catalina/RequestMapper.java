package org.apache.catalina;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import com.techcourse.controller.RootController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper {

    private static final Map<String, Controller> handlerMap = new HashMap<>();

    static {
        handlerMap.put("/login", new LoginController());
        handlerMap.put("/register", new RegisterController());
        handlerMap.put("/", new RootController());
    }

    private RequestMapper() {
    }

    public static Controller getController(HttpRequest request) {
        if (request.isStaticResource()) {
            return new ResourceController();
        }

        return handlerMap.getOrDefault(request.getUri(), new NotFoundController());
    }
}
