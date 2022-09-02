package org.apache.coyote;

import nextstep.jwp.LoginController;

public class ControllerMappings {

    public Controller getAdaptiveController(String path) {

        if (path.equals("/login")) {
            return new LoginController();
        }
        return null;
    }
}
