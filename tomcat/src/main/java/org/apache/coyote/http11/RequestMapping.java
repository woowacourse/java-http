package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import org.apache.controller.Controller;
import org.apache.controller.DefaultController;
import org.apache.controller.LoginController;
import org.apache.controller.RegisterController;
import org.apache.controller.ResourceController;
import org.apache.coyote.request.Request;

public class RequestMapping {

    private static final List<Controller> controllers = new ArrayList<>() {{
        add(new DefaultController());
        add(new LoginController());
        add(new RegisterController());
    }};
    private static final Controller resourceController = new ResourceController();

    public static Controller getController(Request request) {
        return controllers.stream()
                .filter(controller -> controller.support(request))
                .findFirst()
                .orElse(resourceController);
    }
}
