package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Objects;
import nextstep.jwp.handler.ControllerAdvice;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.handler.RequestHandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;

public class HttpFrontServlet {

    private static HttpFrontServlet instance;

    private final RequestHandlerMapping requestHandlerMapping;
    private final ControllerAdvice controllerAdvice;

    public HttpFrontServlet(final RequestHandlerMapping requestHandlerMapping, final ControllerAdvice controllerAdvice) {
        this.requestHandlerMapping = requestHandlerMapping;
        this.controllerAdvice = controllerAdvice;
    }

    public static HttpFrontServlet getInstance() {
        return Objects.requireNonNullElseGet(instance,
                () -> instance = new HttpFrontServlet(new RequestHandlerMapping(), new ControllerAdvice()));
    }

    public ResponseEntity service(final HttpRequest httpRequest) {
        try {
            final HandlerResponseEntity response = handleRequest(httpRequest);
            return createResponseEntity(response);
        } catch (final Exception exception) {
            return controllerAdvice.handleException(exception.getClass());
        }
    }

    private HandlerResponseEntity handleRequest(final HttpRequest httpRequest) {
        final RequestHandler handler = requestHandlerMapping.getHandler(httpRequest.getPath());
        return handler.handle(httpRequest);
    }

    private ResponseEntity createResponseEntity(final HandlerResponseEntity response)
            throws IOException {
        if (response.isEmptyResource()) {
            return ResponseEntity.of(response);
        }

        final String resourcePath = response.getResource();
        if (FileHandler.isStaticFilePath(resourcePath)) {
            return FileHandler.createFileResponse(resourcePath);
        }
        return ResponseEntity.createTextHtmlResponse(response);
    }
}
