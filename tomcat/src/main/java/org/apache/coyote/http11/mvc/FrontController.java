package org.apache.coyote.http11.mvc;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private FrontController() {
    }

    public static void handleHttpRequest(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) throws IOException {
        final Controller controller = RequestMapping.getController(httpRequest.getHttpStartLine());
        controller.service(httpRequest, httpResponse);
    }
}
