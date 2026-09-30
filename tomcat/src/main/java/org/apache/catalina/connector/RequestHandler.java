package org.apache.catalina.connector;

import org.apache.catalina.controller.ControllerMapping;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final ControllerMapping controllerMapping;

    public RequestHandler(ControllerMapping controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public void handle(Http11Processor processor) {
        try {
            var request = processor.readRequest();
            var controller = controllerMapping.getController(request.getRequestTarget());
            var response = controller.service(request);
            processor.writeResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
