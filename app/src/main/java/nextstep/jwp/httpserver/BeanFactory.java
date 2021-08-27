package nextstep.jwp.httpserver;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.dashboard.LoginController;
import nextstep.jwp.dashboard.UserService;
import nextstep.jwp.httpserver.adapter.LoginHandlerAdapter;
import nextstep.jwp.httpserver.adapter.StaticViewHandlerAdapter;
import nextstep.jwp.httpserver.controller.StaticViewController;
import nextstep.jwp.httpserver.mapping.GetHandlerMapping;
import nextstep.jwp.httpserver.mapping.StaticViewHandlerMapping;

public class BeanFactory {
    private static final Map<String, Object> beans = new HashMap<>();
    private static final Map<String, Object> handlerMap = new HashMap<>();

    private BeanFactory() {
    }

    public static void init() {
        final UserService userService = new UserService();
        final LoginController loginController = new LoginController(userService);

        // handlerMapping
        beans.put("staticViewHandlerMapping", new StaticViewHandlerMapping());
        beans.put("getHandlerMapping", new GetHandlerMapping());

        // handlerAdapter
        beans.put("staticViewHandlerAdapter", new StaticViewHandlerAdapter());
        beans.put("loginHandlerAdapter", new LoginHandlerAdapter());

        // handler
        beans.put("staticViewController", new StaticViewController());
        beans.put("userService", userService);
        beans.put("loginController", loginController);

        // handleMap
        handlerMap.put("/login", loginController);
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

    public static Object getHandler(String requestUri) {
        return handlerMap.get(requestUri);
    }
}
