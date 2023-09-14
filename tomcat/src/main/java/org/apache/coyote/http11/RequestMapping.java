package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.request.Request;

public class RequestMapping {

    private static final List<Controller> controllers = List.of(
            new DefaultController(),
            new LoginController(),
            new RegisterController());
    private static final Controller resourceController = new ResourceController();

    private RequestMapping() {}

    public static Controller getController(Request request) {
        return controllers.stream()
                .filter(controller -> controller.support(request))
                .findFirst()
                .orElse(resourceController);
    }
}
