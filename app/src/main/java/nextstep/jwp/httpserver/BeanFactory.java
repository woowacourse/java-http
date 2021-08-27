package nextstep.jwp.httpserver;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.httpserver.adapter.StaticViewHandlerAdapter;
import nextstep.jwp.httpserver.mapping.StaticViewHandlerMapping;

public class BeanFactory {
    private static final Map<String, Object> beans = new HashMap<>();
    private static final Map<String, Object> handlerMap = new HashMap<>();

    private BeanFactory() {
    }

    public static void init() {
        // handlerMapping
        beans.put("staticViewHandlerMapping", new StaticViewHandlerMapping());

        // handlerAdapter
        beans.put("staticViewHandlerAdapter", new StaticViewHandlerAdapter());

        // handler
        //        beans.put("homeController", new HomeController());
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

    public static Object getHandler(String requestUri) {
        return handlerMap.get(requestUri);
    }
}
