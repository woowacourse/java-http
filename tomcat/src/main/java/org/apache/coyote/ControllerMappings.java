package org.apache.coyote;

import nextstep.jwp.LoginController;
import nextstep.jwp.RootController;

public class ControllerMappings {

    public Controller getAdaptiveController(String path) {

        if (path.equals("/")) {
            return new RootController();
        }

        if (path.equals("/login")) {
            return new LoginController();
        }
        return null;
    }
}
