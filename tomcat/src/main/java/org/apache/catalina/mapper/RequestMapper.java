package org.apache.catalina.mapper;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public class RequestMapper {

    private final Controller defaultController = new StaticResourceController();
    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(HttpRequest request) {
        final var uri = request.getURI();
        if (isStaticFileUri(uri)) {
            return defaultController;
        }
        if (controllers.containsKey(uri)) {
            return controllers.get(uri);
        }
        throw new IllegalArgumentException("지원하지 않는 서비스입니다.");
    }

    /**
     * check if uri is for static file or not
     * @param uri request uri text
     * @return whether uri is for static file or not
     */
    private boolean isStaticFileUri(final String uri) {
        final var dotIndex = uri.indexOf(".");
        if (uri.equals("/") || dotIndex > 0 && dotIndex < uri.length() - 1) {
            return true;
        }
        return false;
    }
}
