package org.apache.catalina.web.controller;

import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapper implements RequestMapping {

    private static final Logger log = LoggerFactory.getLogger(RequestMapper.class);
    private final Map<String, Controller> controllerMap;

    public RequestMapper(final Map<String, Controller> controllerMap) {
        this.controllerMap = controllerMap;
    }

    @Override
    public boolean isExistsController(final String resourcePath) {
        return controllerMap.containsKey(resourcePath);
    }

    @Override
    public Controller getController(final String resourcePath) {
        log.debug("요청된 Resource Path: {}", resourcePath);
        return controllerMap.get(resourcePath);
    }
}
