package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.request.MyHttpRequest;

import java.util.Map;

public class RequestMapping {

    // TODO: 외부 주입으로 변경
    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private final Controller staticResourceController = new StaticResourceController();

    public Controller getController(MyHttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
