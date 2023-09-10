package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.apache.coyote.http11.controller.Controller;

public class ExceptionHandler {

    private final Controller methodNotAllowedController;
    private final Controller notFoundController;

    private ExceptionHandler(Controller methodNotAllowedController, Controller notFoundController) {
        validate(methodNotAllowedController, notFoundController);

        this.methodNotAllowedController = methodNotAllowedController;
        this.notFoundController = notFoundController;
    }

    private void validate(Controller methodNotAllowedController, Controller notFoundController) {
        if (methodNotAllowedController == null) {
            throw new IllegalArgumentException("MethodNotAllowedController는 null이 될 수 없습니다.");
        }
        if (notFoundController == null) {
            throw new IllegalArgumentException("NotFoundController는 null이 될 수 없습니다.");
        }
    }

    public static ExceptionHandlerBuilder builder() {
        return new ExceptionHandlerBuilder();
    }

    public void handle(HttpRequest request, HttpResponse response, Exception e) throws Exception {
        if (e instanceof MethodNotAllowedException) {
            methodNotAllowedController.service(request, response);
            List<String> allowedMethods = ((MethodNotAllowedException) e).getAllowedMethods();
            response.setHeader("Allow", allowedMethods);
            return;
        }
        if (e instanceof ResourceNotFoundException) {
            notFoundController.service(request, response);
            return;
        }
        throw e;
    }

    public static class ExceptionHandlerBuilder {
        private Controller methodNotAllowedController;
        private Controller notFoundController;

        public ExceptionHandlerBuilder methodNotAllowedController(Controller controller) {
            this.methodNotAllowedController = controller;
            return this;
        }

        public ExceptionHandlerBuilder notFoundController(Controller controller) {
            this.notFoundController = controller;
            return this;
        }

        public ExceptionHandler build() {
            return new ExceptionHandler(methodNotAllowedController, notFoundController);
        }
    }
}
