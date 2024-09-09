package org.apache.coyote.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.annotaion.GetMapping;
import org.apache.coyote.annotaion.PostMapping;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.UserController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.RequestHandler;

public class AnnotationHandlerMapping implements HandlerMapping {

    private final Map<String, Handler> mappings = new HashMap<>();

    public AnnotationHandlerMapping() {
        registerController(new HomeController());
        registerController(new LoginController());
        registerController(new UserController());
    }

    private void registerController(Controller controller) {
        Class<?> clazz = controller.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping annotation = method.getAnnotation(GetMapping.class);
                mappings.put("GET:" + annotation.value(), new RequestHandler(controller, method));
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping annotation = method.getAnnotation(PostMapping.class);
                mappings.put("POST:" + annotation.value(), new RequestHandler(controller, method));
            }
        }
    }

    @Override
    public Handler getHandler(HttpRequest request) {
        String key = request.getMethod() + ":" + request.getTargetPath();
        return mappings.get(key);
    }
}
