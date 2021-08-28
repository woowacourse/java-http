package nextstep.jwp.httpserver;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.dashboard.controller.LoginController;
import nextstep.jwp.dashboard.controller.RegisterController;
import nextstep.jwp.dashboard.service.UserService;
import nextstep.jwp.httpserver.adapter.ControllerHandlerAdapter;
import nextstep.jwp.httpserver.adapter.StaticResourceHandlerAdapter;
import nextstep.jwp.httpserver.controller.StaticResourceController;
import nextstep.jwp.httpserver.mapping.StaticResourceHandlerMapping;
import nextstep.jwp.httpserver.mapping.UrlMappingHandlerMapping;

public class BeanFactory {
    private static final Map<String, Object> beans = new HashMap<>();
    private static final Map<String, Object> handlerMap = new HashMap<>();

    private BeanFactory() {
    }

    public static void init() {
        customBeanRegister();
        basicBeanRegister();
    }

    private static void customBeanRegister() {
        final UserService userService = new UserService();
        final LoginController loginController = new LoginController(userService);
        final RegisterController registerController = new RegisterController(userService);

        beans.put("userService", userService);
        beans.put("loginController", loginController);
        beans.put("registerController", registerController);

        // handlerMapping
        handlerMap.put("/login", loginController);
        handlerMap.put("/register", registerController);
    }

    private static void basicBeanRegister() {
        // basic controller
        beans.put("staticResourceController", new StaticResourceController());

        // handlerMapping
        beans.put("staticResourceHandlerMapping", new StaticResourceHandlerMapping());
        beans.put("urlMappingHandlerMapping", new UrlMappingHandlerMapping(handlerMap));

        // handlerAdapter
        beans.put("controllerHandlerAdapter", new ControllerHandlerAdapter());
        beans.put("staticResourceHandlerAdapter", new StaticResourceHandlerAdapter());
    }

    public static <T> Map<String, T> findByClassType(Class<T> type) {
        Map<String, T> result = new HashMap<>();

        for (String beanName : beans.keySet()) {
            Object bean = beans.get(beanName);

            if (type.isInstance(bean)) {
                result.put(beanName, (T) bean);
            }
        }
        return result;
    }

    public static Object getBean(String beanName) {
        return beans.get(beanName);
    }
}
