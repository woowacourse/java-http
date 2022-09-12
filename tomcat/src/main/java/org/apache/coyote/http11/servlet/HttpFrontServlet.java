package org.apache.coyote.http11.servlet;

import java.io.IOException;
import nextstep.jwp.handler.ControllerAdvice;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.handler.RequestHandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;

public class HttpFrontServlet {

    private final RequestHandlerMapping requestHandlerMapping;
    private final ControllerAdvice controllerAdvice;

    public HttpFrontServlet() {
        this(new RequestHandlerMapping(), new ControllerAdvice());
    }

    public HttpFrontServlet(final RequestHandlerMapping requestHandlerMapping, final ControllerAdvice controllerAdvice) {
        this.requestHandlerMapping = requestHandlerMapping;
        this.controllerAdvice = controllerAdvice;
    }

    public ResponseEntity service(final HttpRequest httpRequest) {
        try {
            final HandlerResponseEntity response = handleRequest(httpRequest);
            return createResponseEntity(response);
        } catch (final Exception exception) {
            final HttpStatus errorStatus = controllerAdvice.getExceptionStatusCode(exception.getClass());
            return ResponseEntity.createErrorRedirectResponse(HttpStatus.FOUND, errorStatus.getStatusCode() + ".html");
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
