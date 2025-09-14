package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;

public class RequestMapping {

    private static final Map<String, Controller> mappings = new HashMap<>();
    private static final Controller staticResourceController = new StaticResourceController();

    static {
        addController(HttpMethod.GET, "", new DefaultController());
        addController(HttpMethod.GET, "/", new DefaultController());

        addController(HttpMethod.GET, "/index", new IndexController());
        addController(HttpMethod.GET, "/index.html", new IndexController());

        addController(HttpMethod.GET, "/login", new LoginController());
        addController(HttpMethod.POST, "/login", new LoginController());

        addController(HttpMethod.GET, "/register", new RegisterController());
        addController(HttpMethod.POST, "/register", new RegisterController());
    }

    public static void addController(final HttpMethod method, final String path, final Controller controller) {
        mappings.put(path, controller);
    }

    public static Controller getController(Http11Request request) {
        return mappings.getOrDefault(request.getPath(), staticResourceController);
    }
}
