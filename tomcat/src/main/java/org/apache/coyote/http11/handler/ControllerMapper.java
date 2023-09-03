package org.apache.coyote.http11.handler;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.http11.request.HttpRequest;

public class ControllerMapper {

    private static final List<Controller> controllers = new ArrayList<>();

    static {
        controllers.add(new LoginController());
    }

    private ControllerMapper() {
    }

    public static Controller findController(final HttpRequest request) {
        return controllers.stream()
                .filter(each -> each.supports(request))
                .findAny()
                .orElse(null);
    }
}
