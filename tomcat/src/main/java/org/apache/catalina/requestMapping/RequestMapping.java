package org.apache.catalina.requestMapping;

import java.util.Optional;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class RequestMapping {

    private final GetController getController;

    public RequestMapping() {
        this.getController = new GetController();
    }

    public void process(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Optional<Controller> controller = getController.findController(httpRequest);
        if (controller.isEmpty()) {
            httpResponse.init("", ContentType.HTML, HttpStatus.NOT_FOUND);
            return;
        }

        controller.get().service(httpRequest, httpResponse);
    }
}
