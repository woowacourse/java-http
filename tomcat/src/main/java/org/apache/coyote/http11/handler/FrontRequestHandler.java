package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.apache.coyote.http11.HttpStatus.SERVER_ERROR;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.FileHandler;

public class FrontRequestHandler {

    private final RequestHandlerMapping requestHandlerMapping;

    public FrontRequestHandler() {
        this(RequestHandlerMapping.init());
    }

    public FrontRequestHandler(final RequestHandlerMapping requestHandlerMapping) {
        this.requestHandlerMapping = requestHandlerMapping;
    }

    public ResponseEntity handle(final String path, final Map<String, String> queryParams) throws IOException {
        if (!requestHandlerMapping.hasMappingHandler(path)) {
            return FileHandler.createErrorFileResponse(NOT_FOUND);
        }

        final String response;
        try {
            response = requestHandlerMapping.getHandler(path).handle(queryParams);
        } catch (final RuntimeException exception) {
            return FileHandler.createErrorFileResponse(SERVER_ERROR);
        }

        if (FileHandler.isStaticFileResource(response)) {
            return FileHandler.createFileResponse(response);
        }
        return new ResponseEntity(OK, "text/html", response);
    }
}
