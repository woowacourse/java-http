package org.apache.coyote.http11.web;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseStatus;

public class FrontController {

    private static final Set<String> RESOURCE_PREFIX = Set.of(
            "/css", "/js", "/images", "/fonts", "/favicon.ico", "/assets"
    );

    private final HandlerMapping handlerMapping;

    public FrontController(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpRequestStartLine httpStartLine = httpRequest.getHttpStartLine();
        if (isResourceURI(httpStartLine)) {
            final String accept = httpRequest.getHeader("Accept");
            httpResponse.setHeader("Content-Type", accept);
            httpResponse.setBody(new View(httpStartLine.getRequestURI()).renderView());
            return;
        }

        final Optional<Controller> controller = handlerMapping.findController(httpStartLine);
        final View view = controller.map(c -> c.handleRequest(httpRequest, httpResponse))
                .orElseGet(() -> isNotExistResource(httpResponse));

        httpResponse.setHeader("Content-Type", view.getContentType());
        httpResponse.setBody(view.renderView());
    }

    private boolean isResourceURI(final HttpRequestStartLine httpStartLine) {
        for (final String resourcePrefix : RESOURCE_PREFIX) {
            if (httpStartLine.getRequestURI().startsWith(resourcePrefix)) {
                return true;
            }
        }
        return false;
    }

    private View isNotExistResource(final HttpResponse httpResponse) {
        httpResponse.updateHttpResponseStatusLineByStatus(HttpResponseStatus.NOT_FOUND);
        return new View("404.html");
    }
}
