package org.apache.catalina.handler;

import java.util.Map;
import org.apache.catalina.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(HandlerMapping.class);
    private final Map<String, Controller> controllerMap;

    public HandlerMapping(Map<String, Controller> controllerMap) {
        this.controllerMap = controllerMap;
    }

    public boolean existsController(String resourcePath) {
        return controllerMap.containsKey(resourcePath);
    }

    public Controller getController(final String resourcePath) {
        log.debug("요청된 Resource Path: {}", resourcePath);
        return controllerMap.get(resourcePath);
    }
}
