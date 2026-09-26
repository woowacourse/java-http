package com.techcourse;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.http.HttpRequest;
import com.techcourse.resource.StaticResourceLoader;
import java.util.Map;
import org.apache.catalina.SessionManager;

public class RequestMapping {

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapping(SessionManager sessionManager, StaticResourceLoader staticResourceLoader) {
        this.controllers = Map.of(
                "/", new HomeController(),
                "/login", new LoginController(staticResourceLoader),
                "/register", new RegisterController(staticResourceLoader)
        );
        this.staticResourceController = new StaticResourceController(staticResourceLoader);
    }

    public Controller getController(HttpRequest request) throws Exception{
        String path = request.getRequestLine().getPath();

        return controllers.getOrDefault(
                path,
                staticResourceController
        );
    }
}
