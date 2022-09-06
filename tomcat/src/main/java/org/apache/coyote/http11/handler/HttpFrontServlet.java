package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpFrontServlet {

    private final RequestServletMapping requestHandlerMapping;

    public HttpFrontServlet() {
        this(RequestServletMapping.init());
    }

    public HttpFrontServlet(final RequestServletMapping requestServletMapping) {
        this.requestHandlerMapping = requestServletMapping;
    }

    public ResponseEntity service(final String path, final HttpRequest httpRequest) throws IOException {
        try {
            final ServletResponseEntity response = handleRequest(path, httpRequest);
            return createResponseEntity(response);
        } catch (final Exception exception) {
            final ServletAdvice servletAdvice = ServletAdvice.init();
            return servletAdvice.handleException(exception.getClass());
        }
    }

    private ServletResponseEntity handleRequest(final String path, final HttpRequest httpRequest) {
        final RequestServlet handler = requestHandlerMapping.getHandler(path);
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
