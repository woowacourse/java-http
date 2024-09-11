package org.apache.catalina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.util.StaticResourceReader;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapper implements RequestMapping {

    private static final Logger log = LoggerFactory.getLogger(RequestMapper.class);

    private final Map<String, Controller> controllers;
    private final Controller staticResourceController;

    public RequestMapper() {
        this.staticResourceController = new StaticResourceController();
        this.controllers = new HashMap<>();
    }

    @Override
    public Optional<Controller> getController(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getPath();

        if (controllers.containsKey(path)) {
            return Optional.of(controllers.get(path));
        }
        if (StaticResourceReader.isExist(path)) {
            return Optional.of(staticResourceController);
        }
        log.warn("Handler not found for {} {}", method, path);
        return Optional.empty();
    }

    @Override
    public void register(AbstractController controller) {
        controllers.put(controller.matchedPath(), controller);
    }

    public void registerControllers(List<Class<? extends AbstractController>> controllerClasses) {
        controllerClasses.stream()
                .map(this::instantiateController)
                .forEach(this::register);
    }

    private AbstractController instantiateController(Class<? extends AbstractController> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to instantiate controller: " + clazz, e);
        }
    }
}
