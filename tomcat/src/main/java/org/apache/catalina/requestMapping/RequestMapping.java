package org.apache.catalina.requestMapping;

import java.util.Optional;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class RequestMapping {

    private final getController getController;

    public RequestMapping() {
        this.getController = new getController();
    }

    public void process(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Optional<Controller> servlet = getController.findServlet(httpRequest);
        if (servlet.isEmpty()) {
            httpResponse.init("", ContentType.HTML, HttpStatus.NOT_FOUND);
            return;
        }

        servlet.get().service(httpRequest, httpResponse);
    }
}
