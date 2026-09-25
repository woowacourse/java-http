package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import org.apache.coyote.request.MyHttpRequest;

import java.util.Map;

public class RequestMapping {

    Map<String, Controller> controllers = Map.of(
            "/login", new LoginController()
    );

    public Controller getController(MyHttpRequest request) {
        return controllers.get(request.getPath());
    }
}
