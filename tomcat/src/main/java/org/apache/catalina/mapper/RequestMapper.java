package org.apache.catalina.mapper;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.request.Http11Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapper {

    private static final Logger log = LoggerFactory.getLogger(RequestMapper.class);
    private final Controller defaultController;
    private final Map<String, Controller> controllerMap;

    public RequestMapper() {
        this.controllerMap = new HashMap<>();
        this.defaultController = new DefaultController();
        initializeControllers();
    }

    public Controller findController(Http11Request request) {
        log.debug("요청된 Resource Path: {}", request.parseResourcePath());
        return controllerMap.getOrDefault(request.parseResourcePath(), defaultController);
    }

    private void initializeControllers() {
        //Todo: application 의존성 분리하기 [2025-09-05 17:17:18]
        controllerMap.put("/login", new UserController());
    }
}
