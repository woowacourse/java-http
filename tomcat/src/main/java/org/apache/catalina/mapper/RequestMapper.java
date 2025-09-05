package org.apache.catalina.mapper;

import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.request.Http11Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapper {

    private static final Logger log = LoggerFactory.getLogger(RequestMapper.class);
    private final Map<String, Controller> controllerMap;

    public RequestMapper() {
        this.controllerMap = new HashMap<>();
        initializeControllers();
    }

    public Controller findController(Http11Request request) {
        return controllerMap.get(request.parseResourcePath());
    }

    private void initializeControllers() {
        controllerMap.put("/login", new UserController());
    }
}
