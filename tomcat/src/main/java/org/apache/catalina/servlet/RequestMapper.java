package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Set;
import org.apache.catalina.http.HttpRequest;

public class RequestMapper {

    private static final Set<AbstractController> controllers = Set.of(
            new LoginController(),
            new RegisterController()
    );

    private static final AbstractController resourceController = new ResourceController();

    private RequestMapper() {
    }

    public static AbstractController getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller ->
                        request.URIStartsWith(controller.getClass().getAnnotation(RequestMapping.class).value())
                )
                .findAny()
                .orElse(resourceController);
    }
}
