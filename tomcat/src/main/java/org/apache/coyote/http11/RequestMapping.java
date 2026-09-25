package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void add(String path, Controller controller) {
        controllers.put(Objects.requireNonNull(path), Objects.requireNonNull(controller));
    }

    public Controller getController(HttpRequest request) {
        return controllers.get(request.getPath());
    }
}
