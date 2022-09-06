package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.handler.ServletAdvice;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;

public class HttpFrontServlet {

    private final RequestServletMapping requestHandlerMapping;

    public HttpFrontServlet() {
        this(RequestServletMapping.init());
    }

    public HttpFrontServlet(final RequestServletMapping requestServletMapping) {
        this.requestHandlerMapping = requestServletMapping;
    }

    public ResponseEntity service(final HttpRequest httpRequest) throws IOException {
        try {
            final ServletResponseEntity response = handleRequest(httpRequest);
            return createResponseEntity(response);
        } catch (final Exception exception) {
            final ServletAdvice servletAdvice = ServletAdvice.init();
            final HttpStatus errorStatus = servletAdvice.getExceptionStatusCode(exception.getClass());
            return ResponseEntity.createRedirectResponse(HttpStatus.FOUND, errorStatus.getStatusCode() + ".html");
        }
    }

    private ServletResponseEntity handleRequest(final HttpRequest httpRequest) {
        final RequestServlet handler = requestHandlerMapping.getHandler(httpRequest.getPath());
        // doGet, doPost
        return handler.doPost(httpRequest);
    }

    private ResponseEntity createResponseEntity(final ServletResponseEntity response)
            throws IOException {
        if (response.getResource() == null) {
            return ResponseEntity.of(response);
        }

        if (FileHandler.isStaticFileResource(response.getResource())) {
            return FileHandler.createFileResponse(response);
        }
        return ResponseEntity.createHtmlResponse(response);
    }
}
