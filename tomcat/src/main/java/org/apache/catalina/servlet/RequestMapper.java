package org.apache.catalina.servlet;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import com.techcourse.controller.RestController;
import java.util.Set;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapper {

    private final Set<RestController> controllers;
    private final Controller resourceController;

    public RequestMapper() {
        this.resourceController = new ResourceController();
        this.controllers = Set.of(
                new LoginController(),
                new RegisterController()
        );
    }

    public Controller getController(HttpRequest request) {
        if (request.isTargetBlank() || request.isTargetStatic()) {
            return resourceController;
        }

        return controllers.stream()
                .filter(controller ->
                        request.uriStartsWith(controller.getClass().getAnnotation(RequestMapping.class).value())
                )
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot map controller"));
    }
}
