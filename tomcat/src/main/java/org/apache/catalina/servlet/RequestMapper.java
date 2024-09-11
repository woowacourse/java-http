package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Set;
import org.apache.catalina.http.HttpRequest;

public class RequestMapper {

    private static final Set<Controller> controllers = Set.of(
            new LoginController(),
            new RegisterController()
    );

    private static final Controller resourceController = new ResourceController();

    private RequestMapper() {
    }

    public static Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller ->
                        request.uriStartsWith(controller.getClass().getAnnotation(RequestMapping.class).value())
                )
                .findAny()
                .orElse(resourceController);
    }
}
