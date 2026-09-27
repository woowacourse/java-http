package com.techcourse.web;

import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.RegisterController;
import com.techcourse.web.controller.StaticResourceController;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.HttpRequest;

/**
 * 요청 경로에 매핑된 Controller를 찾아주고, 없으면 기본 컨트롤러를 반환함.
 */
public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final Controller DEFAULT_CONTROLLER = new StaticResourceController();

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return CONTROLLERS.getOrDefault(path, DEFAULT_CONTROLLER);
    }
}
