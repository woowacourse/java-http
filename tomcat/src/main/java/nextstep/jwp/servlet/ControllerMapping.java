package nextstep.jwp.servlet;

import nextstep.jwp.controller.*;
import nextstep.jwp.exception.CustomNotFoundException;
import org.apache.catalina.support.ResourceSuffix;
import org.apache.coyote.support.RequestInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerMapping {

    private final Map<String, Controller> mapping = new ConcurrentHashMap<>();

    public ControllerMapping() {
        mapping.put("/", new GreetingController());
        mapping.put("/login", new LoginController(() -> UUID.randomUUID().toString()));
        mapping.put("/register", new RegisterController());
    }

    public Controller getController(final RequestInfo requestInfo) {
        final Controller controller = mapping.get(requestInfo.getUri());
        if (controller != null) {
            return controller;
        }
        if (isResourceUri(requestInfo.getUri())) {
            return new ResourceController();
        }
        throw new CustomNotFoundException(requestInfo.getUri() + "를 처리할 컨트롤러를 찾지 못함");
    }

    private boolean isResourceUri(final String uri) {
        return ResourceSuffix.isEndWith(uri);
    }
}
