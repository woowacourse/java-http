package org.apache.coyote.http11.mvc;

import java.io.IOException;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private final ControllerMapping controllerMapping;

    public FrontController(final ControllerMapping controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Controller controller = controllerMapping.findController(httpRequest.getHttpStartLine());
        final ResponseEntity response = controller.handleRequest(httpRequest, httpResponse);
        httpResponse.updateByResponseEntity(response);
    }
}
