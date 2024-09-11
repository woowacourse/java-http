package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final FrontController instance = new FrontController();
    private static final Map<String, Controller> controllerMap = new HashMap<>();
    private static final Controller defaultController = new ResourceController();


    static {
        controllerMap.put("/", new WelcomeController());
        controllerMap.put("/index", new IndexController());
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    private FrontController() {
    }

    public static FrontController getInstance() {
        return instance;
    }

    public Controller mapController(String path) {
        Controller controller = controllerMap.get(path);
        if(controller == null) {
            return defaultController;
        }
        return controller;
    }
}
