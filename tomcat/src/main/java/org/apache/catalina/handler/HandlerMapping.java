package org.apache.catalina.handler;

import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.exception.PathNotFoundException;
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

        Controller controller = controllerMap.get(resourcePath);

        if (controller == null) {
            log.warn("Path:{} 에 매핑된 Controller 가 없습니다.", resourcePath);
            throw new PathNotFoundException("매핑된 Controller 가 없습니다.");
        }
        return controller;
    }
}
