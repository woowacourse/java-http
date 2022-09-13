package org.apache.catalina;

import java.util.List;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.Container;
import org.apache.coyote.Controller;
import org.apache.coyote.ExceptionController;
import org.apache.coyote.http11.request.Request;

public class ControllerContainer implements Container {
    private final RequestMapping requestMapping;
    private final List<ExceptionController> exceptionControllers;

    public ControllerContainer(final RequestMapping requestMapping, final List<ExceptionController> exceptionControllers) {
        this.requestMapping = requestMapping;
        this.exceptionControllers = exceptionControllers;
    }

    @Override
    public Controller findController(final Request request) {
        return requestMapping.map(request.getURL());
    }

    @Override
    public ExceptionController findExceptionHandler(final Exception exception) {
        return exceptionControllers.stream()
                .filter(exceptionController -> exceptionController.isResolvable(exception))
                .findFirst()
                .orElseThrow(ControllerNotFoundException::new);
    }
}
