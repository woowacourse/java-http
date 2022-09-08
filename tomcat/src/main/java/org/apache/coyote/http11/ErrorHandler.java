package org.apache.coyote.http11;

import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.util.ResourceUtil;

public class ErrorHandler {

    private static final String TEXT_HTML = "text/html";

    public void handle(final Exception exception, final HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED)
                .setContentType(TEXT_HTML)
                .setResponseBody(ErrorResource.UNAUTHORIZED.getResource());
    }

    private enum ErrorResource {
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "/401.html"),
        NOT_FOUND(HttpStatus.NOT_FOUND, "/404.html"),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"),
        ;

        private final HttpStatus httpStatus;
        private final String resource;

        ErrorResource(final HttpStatus httpStatus, final String resource) {
            this.httpStatus = httpStatus;
            this.resource = resource;
        }

        public String getResource() {
            return ResourceUtil.getResource(resource);
        }
    }
}
