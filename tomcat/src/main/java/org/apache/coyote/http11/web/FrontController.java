package org.apache.coyote.http11.web;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseStatus;

public class FrontController {
    private final HandlerMapping handlerMapping;

    public FrontController(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Optional<Controller> controller = handlerMapping.findController(httpRequest.getHttpStartLine());
        final View view = controller.map(c -> c.handleRequest(httpRequest, httpResponse))
                .orElseGet(() -> isNotExistResource(httpResponse));

        httpResponse.setHeader("Content-Type", view.getContentType());
        httpResponse.setBody(view.renderView());
    }

    private View isNotExistResource(final HttpResponse httpResponse) {
        httpResponse.updateHttpResponseStatusLineByStatus(HttpResponseStatus.NOT_FOUND);
        return new View("404.html");
    }
}
