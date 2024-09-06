package org.apache.coyote.http11.request.mapper;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.controller.model.Controller;
import java.util.List;
import org.apache.coyote.http11.request.model.HttpRequest;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping() {
        this.controllers = initControllers();
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(httpRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    private List<Controller> initControllers() {
        return List.of(
                new DefaultController(),
                new LoginController(),
                new StaticResourceController()
        );
    }
}
