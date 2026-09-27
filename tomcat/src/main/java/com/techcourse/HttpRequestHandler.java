package com.techcourse;

import com.techcourse.controller.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final RequestMapping requestMapping;

    public HttpRequestHandler(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void handle(Http11Processor processor) {
        try {
            var request = processor.readRequest();
            var controller = requestMapping.getController(request.getRequestTarget());
            var response = controller.service(request);
            processor.writeResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
