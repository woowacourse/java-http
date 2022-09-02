package org.apache.coyote;

import nextstep.jwp.LoginController;

public class HandlerMappings {

    public Handler getAdaptiveHandler(String path) {

        if (path.equals("/login")) {
            return new LoginController();
        }
        return null;
    }
}
