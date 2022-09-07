package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpMethod.*;

import java.io.IOException;
import java.util.HashMap;
import nextstep.jwp.handler.ServletAdvice;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;

public class HttpFrontServlet {

    private final RequestServletMapping requestHandlerMapping;
    private final ServletAdvice servletAdvice;

    public HttpFrontServlet() {
        this(RequestServletMapping.init(), ServletAdvice.init());
    }

    public HttpFrontServlet(final RequestServletMapping requestServletMapping, final ServletAdvice servletAdvice) {
        this.requestHandlerMapping = requestServletMapping;
        this.servletAdvice = servletAdvice;
    }

    public ResponseEntity service(final HttpRequest httpRequest) throws IOException {
        try {
            final ServletResponseEntity response = handleRequest(httpRequest);
            return createResponseEntity(response);
        } catch (final Exception exception) {
            final HttpStatus errorStatus = servletAdvice.getExceptionStatusCode(exception.getClass());
            return ResponseEntity.createRedirectResponse(HttpStatus.FOUND, errorStatus.getStatusCode() + ".html");
        }
    }

    private ServletResponseEntity handleRequest(final HttpRequest httpRequest) {
        final RequestServlet handler = requestHandlerMapping.getHandler(httpRequest.getPath());

        final HttpResponseHeader responseHeader = new HttpResponseHeader(new HashMap<>());
        if (httpRequest.getHttpStatus().isSame(GET)) {
            return handler.doGet(httpRequest, responseHeader);
        }
        return handler.doPost(httpRequest, responseHeader);
    }

    private ResponseEntity createResponseEntity(final ServletResponseEntity response)
            throws IOException {
        if (response.getResource() == null) {
            return ResponseEntity.of(response);
        }

        if (FileHandler.isStaticFileResource(response.getResource())) {
            return FileHandler.createFileResponse(response);
        }
        return ResponseEntity.createTextHtmlResponse(response);
    }
}
