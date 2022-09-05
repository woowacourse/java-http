package nextstep.jwp.servlet.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.servlet.request.HttpRequest;

public class HandlerMapping {

    private final Map<String, Controller> mappings;

    public HandlerMapping(Map<String, Controller> mappings) {
        this.mappings = mappings;
    }

    public static HandlerMapping of(Controller... controllers) {
        Map<String, Controller> mappings = new HashMap<>();
        for (final var controller : controllers) {
            mapPathToController(mappings, controller);
        }
        return new HandlerMapping(mappings);
    }

    private static void mapPathToController(Map<String, Controller> mappings, Controller controller) {
        Class<?> clazz = controller.getClass();
        RequestMapping declaredAnnotation = clazz.getDeclaredAnnotation(RequestMapping.class);
        for (final var path : declaredAnnotation.path()) {
            mappings.put(path, controller);
        }
    }

    public Controller getMappedHandler(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.get(uri);
    }

    public boolean hasMappedHandler(HttpRequest request) {
        final var uri = request.getUri();
        return mappings.containsKey(uri);
    }
}
