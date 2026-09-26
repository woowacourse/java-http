package org.apache.catalina.mapper;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> mappings = new HashMap<>();
    private final Controller staticResourceController = new StaticResourceController();

    public RequestMapping() {
        addMapping("/login", new LoginController());
        addMapping("/register", new RegisterController());
    }

    public void addMapping(final String path, final Controller controller) {
        mappings.put(path, controller);
    }

    public Controller getController(final HttpRequest request) {
        final String target = request.getRequestLine().getTarget();
        final String path = URI.create(target).getPath();
        return mappings.getOrDefault(path, staticResourceController);
    }
}
