package org.apache.catalina.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.servlet.controller.HomeController;
import org.apache.catalina.servlet.controller.LoginController;
import org.apache.catalina.servlet.controller.StaticResourceController;
import org.apache.catalina.servlet.controller.UserRegistrationController;

public class RequestMapping {

    private final Map<String, Controller> registry;

    public RequestMapping() {
        registry = new ConcurrentHashMap<>();
        registry.put("/", new HomeController());
        registry.put("/login", new LoginController());
        registry.put("/register", new UserRegistrationController());
    }

    public void add(final String uriPath, final Controller controller) {
        registry.put(uriPath, controller);
    }

    public Controller get(final String uriPath) {
        if (!registry.containsKey(uriPath)) {
            return new StaticResourceController(uriPath);
        }
        return registry.get(uriPath);
    }
}
