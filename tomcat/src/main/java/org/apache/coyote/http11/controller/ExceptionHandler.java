package org.apache.coyote.http11.controller;

import java.util.List;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.exception.ResourceNotFoundException;

public class ExceptionHandler {

    private final Controller methodNotAllowedController;
    private final Controller notFoundController;

    private ExceptionHandler(Controller methodNotAllowedController, Controller notFoundController) {
        this.methodNotAllowedController = methodNotAllowedController;
        this.notFoundController = notFoundController;
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
