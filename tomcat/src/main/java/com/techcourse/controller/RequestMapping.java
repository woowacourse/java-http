package com.techcourse.controller;

import com.techcourse.exception.client.NotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new ConcurrentHashMap<>();
    private static final Controller viewController = new ViewController();

    public RequestMapping() {
        controllerMap.put("/", new MainPageController());
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".ico")) {
            return viewController;
        }
        return controllerMap.keySet().stream()
                .filter(controllerPath -> controllerPath.equals(path))
                .findAny()
                .map(controllerMap::get)
                .orElseThrow(() -> new NotFoundException("리소스를 처리할 컨트롤러가 없습니다."));
    }
}
