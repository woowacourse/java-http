package org.apache.catalina.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// RequestMapping은 SpringMvc 구현 개념이지만, 현재 실습에선 편의상 Catalina 내 포함시킵니다.
public class RequestMapping {

    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);


    private final Map<String, Controller> controllerMap;

    public RequestMapping(final Map<String, Controller> controllerMap) {
        this.controllerMap = controllerMap;
    }

    public boolean existsController(final String resourcePath) {
        return controllerMap.containsKey(resourcePath);
    }

    public Controller getController(final String resourcePath) {
        log.debug("요청된 Resource Path: {}", resourcePath);
        return controllerMap.get(resourcePath);
    }
}
