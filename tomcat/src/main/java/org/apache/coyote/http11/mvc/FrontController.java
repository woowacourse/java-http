package org.apache.coyote.http11.mvc;

import java.io.IOException;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private final RequestMapping requestMapping;

    public FrontController(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Controller controller = requestMapping.getController(httpRequest.getHttpStartLine());
        final ResponseEntity response = controller.service(httpRequest, httpResponse);
        httpResponse.updateByResponseEntity(response);
    }
}
