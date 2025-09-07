package org.apache.coyote.http11.dispatcher.handlerAdapter;

import com.techcourse.Controller;
import com.techcourse.Service;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.dispatcher.HandlerMethod;
import org.apache.coyote.http11.dispatcher.RouteKey;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MethodHandlerAdapter implements HandlerAdapter {

    private final static Map<RouteKey, HandlerMethod> mappings = new ConcurrentHashMap<>();

    public MethodHandlerAdapter() {
        init();
    }

    public static void init() {
        Controller controller = new Controller(new Service());

        mappings.put(new RouteKey("GET", "/"),
                new HandlerMethod(controller, method(controller, "hello")));
        mappings.put(new RouteKey("POST", "/login"),
                new HandlerMethod(controller, method(controller, "signIn", Map.class))
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
        String method = httpRequest.getMappingLine().getRequestMapping();
        String path = httpRequest.getMappingLine().getUrl();
        return mappings.containsKey(new RouteKey(method, path));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String method = httpRequest.getMappingLine().getRequestMapping();
        String path = httpRequest.getMappingLine().getUrl();
        HandlerMethod handlerMethod = mappings.get(new RouteKey(method, path));
        return handlerMethod.invoke(httpRequest);
    }
}
