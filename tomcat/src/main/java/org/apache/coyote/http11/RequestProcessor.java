package org.apache.coyote.http11;

import com.techcourse.controller.model.Controller;
import java.util.Optional;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.mapper.RequestMapping;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

public class RequestProcessor {

    private final RequestMapping requestMapping;

    public RequestProcessor() {
        this.requestMapping = new RequestMapping();
    }

    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<Controller> mappedController = requestMapping.findController(httpRequest);

        if (mappedController.isEmpty()) {
            httpResponse.sendError(HttpStatus.NOT_FOUND);
        }
        if (mappedController.isPresent()) {
            Controller controller = mappedController.get();
            controller.service(httpRequest, httpResponse);
        }
    }
}
