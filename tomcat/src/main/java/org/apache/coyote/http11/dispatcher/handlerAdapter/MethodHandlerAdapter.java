package org.apache.coyote.http11.dispatcher.handlerAdapter;

import com.techcourse.RestController;
import com.techcourse.Service;
import com.techcourse.ViewController;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.dispatcher.HandlerMethod;
import org.apache.coyote.http11.dispatcher.RouteKey;
import org.apache.coyote.http11.request.HttpRequest;

public class MethodHandlerAdapter implements HandlerAdapter {

    private final static Map<RouteKey, HandlerMethod> mappings = new ConcurrentHashMap<>();

    public MethodHandlerAdapter() {
        init();
    }

    public static void init() {
        RestController restController = new RestController(new Service());
        mappings.put(new RouteKey("GET", "/"),
                new HandlerMethod(restController, method(restController, "hello")));
        mappings.put(new RouteKey("POST", "/login"),
                new HandlerMethod(restController, method(restController, "signIn", Map.class))
        );

        ViewController viewController = new ViewController();
        mappings.put(new RouteKey("GET", "/login"),
                new HandlerMethod(viewController, method(viewController, "getLoginPage"))
        );
        mappings.put(new RouteKey("GET", "/register"),
                new HandlerMethod(viewController, method(viewController, "getRegisterPage"))
        );
    }

    private static Method method(Object object, String name, Class<?>... p) {
        try {
            return object.getClass().getDeclaredMethod(name, p);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        String method = httpRequest.getMappingLine().getMethod();
        String path = httpRequest.getMappingLine().getUrl();
        return mappings.containsKey(new RouteKey(method, path));
    }

    @Override
    public Object handle(HttpRequest httpRequest) {
        String method = httpRequest.getMappingLine().getMethod();
        String path = httpRequest.getMappingLine().getUrl();
        HandlerMethod handlerMethod = mappings.get(new RouteKey(method, path));
        return handlerMethod.invoke(httpRequest);
    }
}
