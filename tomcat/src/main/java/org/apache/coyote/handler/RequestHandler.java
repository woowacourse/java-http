package org.apache.coyote.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.dynamic.HomeController;
import org.apache.coyote.controller.dynamic.LoginController;
import org.apache.coyote.controller.dynamic.RegisterController;
import org.apache.coyote.controller.resource.ResourceType;
import org.apache.coyote.controller.resource.StaticController;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;

public class RequestHandler {

    private static Controller staticController;
    private static Map<String, Controller> dynamicControllers;

    static {
        staticController = new StaticController();
        dynamicControllers = new HashMap<>();
        dynamicControllers.put("/", new HomeController());
        dynamicControllers.put("/login", new LoginController());
        dynamicControllers.put("/register", new RegisterController());
    }

    public static Controller getController(final String path) {
        Controller findController = dynamicControllers.getOrDefault(path, null);
        if (ResourceType.isStaticRequest(path)) {
            findController = staticController;
        }
        if (findController != null) {
            return findController;
        }
        throw new HttpException(ErrorCode.NOT_ALLOW_PATH);
    }
}
