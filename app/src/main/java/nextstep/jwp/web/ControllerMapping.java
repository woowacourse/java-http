package nextstep.jwp.web;

import nextstep.jwp.ApplicationContext;
import nextstep.jwp.exception.NoMatchingHandlerException;
import nextstep.jwp.presentation.Controller;

public class ControllerMapping {

    private ControllerMapping() {
    }

    public static Controller getController(String url) {
        if (!ApplicationContext.getControllers().containsKey(url)) {
            throw new NoMatchingHandlerException();
        }

        return ApplicationContext
            .getControllers().get(url);
    }
}

