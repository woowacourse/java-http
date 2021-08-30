package nextstep.jwp.web;

import nextstep.jwp.ApplicationContext;
import nextstep.jwp.exception.NoMatchingHandlerException;
import nextstep.jwp.presentation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerMapping {

    public static final Logger logger = LoggerFactory.getLogger(ControllerMapping.class);

    private ControllerMapping() {
    }

    public static Controller getController(String url) {
        if (!ApplicationContext.getControllers().containsKey(url)) {
            logger.error("No matching Handler for {}", url);
            throw new NoMatchingHandlerException();
        }

        return ApplicationContext
            .getControllers().get(url);
    }
}

