package org.apache.coyote.http11;

import com.techcourse.controller.model.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.mapper.RequestMapping;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

import java.util.Optional;

public class RequestProcessor {

    private final RequestMapping requestMapping;

    public RequestProcessor() {
        this.requestMapping = new RequestMapping();
    }

    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<Controller> mappedController = requestMapping.findController(httpRequest);

        mappedController.ifPresentOrElse(controller -> controller.service(httpRequest, httpResponse),
                () -> httpResponse.sendError(HttpStatus.NOT_FOUND));
    }
}
