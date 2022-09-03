package nextstep.jwp.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.support.HttpMethod;

public class HandlerMappings {

    private final Map<MappedHandler, Handler> mappings;

    public HandlerMappings(Map<MappedHandler, Handler> mappings) {
        this.mappings = mappings;
    }

    public static HandlerMappings of(Object... controllers) {
        Map<MappedHandler, Handler> mappings = new HashMap<>();
        for (final var controller : controllers) {
            mapValidHandlers(mappings, controller);
        }
        return new HandlerMappings(mappings);
    }

    private static void mapValidHandlers(Map<MappedHandler, Handler> mappings, Object controller) {
        Class<?> clazz = controller.getClass();
        for (final var method : clazz.getMethods()) {
            mapValidHandlers(mappings, controller, method);
        }
    }

    private static void mapValidHandlers(Map<MappedHandler, Handler> mappings, Object controller, Method method) {
        final var annotation = method.getAnnotation(RequestMapping.class);
        if (annotation == null) {
            return;
        }
        for (final var uri : annotation.path()) {
            mappings.put(new MappedHandler(uri, annotation.method()), new Handler(method, controller));
        }
    }

    public Handler getHandler(HttpRequest request) {
        final var uri = request.getUri();
        final var method = request.getMethod();
        MappedHandler handlerKey = new MappedHandler(uri, method);
        if (!mappings.containsKey(handlerKey)) {
            return null;
        }
        return mappings.get(handlerKey);
    }

    private static class MappedHandler {

        final String uri;
        final HttpMethod method;

        public MappedHandler(String uri, HttpMethod method) {
            this.uri = uri;
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MappedHandler that = (MappedHandler) o;
            return Objects.equals(uri, that.uri) && method == that.method;
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, method);
        }
    }
}
