package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private final Map<String, Controller> pathControllerMap;

    public RequestMapping() {
        this.pathControllerMap = new HashMap<>();
    }

    public void putController(String requestUri, Controller controller) {
        pathControllerMap.put(requestUri, controller);
    }

    public Optional<Controller> findController(String requestUri) {
        return Optional.ofNullable(pathControllerMap.get(requestUri));
    }
}
