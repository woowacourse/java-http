package org.apache.catalina.parser;

import java.util.List;

import org.apache.catalina.mvc.Controller;
import org.apache.catalina.request.HttpRequest;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.PageLoadController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;

public class RequestMapping {

    private final List<Controller> controllers;

    public RequestMapping() {
        this.controllers = List.of(
                new RootController(),
                new PageLoadController(),
                new LoginController(),
                new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.isMatchesRequest(request))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("연결된 컨트롤러가 없습니다."));
    }
}
