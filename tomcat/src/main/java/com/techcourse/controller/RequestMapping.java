package com.techcourse.controller;

import java.util.List;
import org.apache.controller.Controller;
import org.apache.exception.DataNotFoundException;
import org.apache.http.request.HttpRequest;

public class RequestMapping {

    private final List<Controller> controllers = List.of(
            new LoginController(),
            new RegisterController(),
            new RootController(),
            new StaticFileController()
    );

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canProcessable(request))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 요청입니다."));
    }
}
