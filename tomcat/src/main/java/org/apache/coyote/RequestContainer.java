package org.apache.coyote;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.container.ServletContainer;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.mapper.RequestMapping;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

import java.awt.*;
import java.util.Optional;

public class RequestContainer extends Container implements ServletContainer {

    private final RequestMapping requestMapping;

    public RequestContainer() {
        this.requestMapping = new RequestMapping();
    }

    @Override
    public void invoke(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<Controller> mappedController = requestMapping.findController(httpRequest);

        mappedController.ifPresentOrElse(controller -> controller.service(httpRequest, httpResponse),
                () -> httpResponse.sendError(HttpStatus.NOT_FOUND));
    }
}
