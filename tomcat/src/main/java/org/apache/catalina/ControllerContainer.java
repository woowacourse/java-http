package org.apache.catalina;

import java.util.List;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.Controller;
import org.apache.coyote.ControllerFinder;
import org.apache.coyote.ExceptionController;
import org.apache.coyote.http11.request.Request;

public class ControllerContainer implements ControllerFinder {
    private final List<Controller> controllers;
    private final List<ExceptionController> exceptionControllers;

    public ControllerContainer(final List<Controller> controllers, final List<ExceptionController> exceptionControllers) {
        this.controllers = controllers;
        this.exceptionControllers = exceptionControllers;
    }

    @Override
    public Controller findController(final Request request) {
        return controllers.stream()
                .filter(controller -> controller.isRunnable(request))
                .findFirst()
                .orElseThrow(ControllerNotFoundException::new);
    }

    @Override
    public ExceptionController findExceptionHandler(final Exception exception) {
        return exceptionControllers.stream()
                .filter(exceptionController -> exceptionController.isResolvable(exception))
                .findFirst()
                .orElseThrow(ControllerNotFoundException::new);
    }
}
