package org.apache.coyote.http11.handler;

import java.util.HashMap;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.ResponseEntity;

public class ServletResponseEntity extends ResponseEntity {

    private static final String EMPTY_BODY = "";

    private final String resource;

    public ServletResponseEntity(final HttpStatus httpStatus, final HttpResponseHeader headers, final String body,
                                 final String resource) {
        super(httpStatus, headers, body);
        this.resource = resource;
    }

    public static ServletResponseEntity createWithResource(final HttpResponseHeader headers, final String resource) {
        return new ServletResponseEntity(HttpStatus.OK, headers, EMPTY_BODY, resource);
    }

    public static ServletResponseEntity createWithResource(final String resource) {
        return createWithResource(new HttpResponseHeader(new HashMap<>()), resource);
    }

    public static ServletResponseEntity createRedirectResponse(final HttpStatus httpStatus,
                                                               final HttpResponseHeader headers, final String path) {
        headers.addHeader("Location", path);
        return createResponseBody(httpStatus, headers, EMPTY_BODY);
    }

    public static ServletResponseEntity createResponseBody(final HttpStatus httpStatus,
                                                           final HttpResponseHeader headers, final String body) {
        return new ServletResponseEntity(httpStatus, headers, body, EMPTY_BODY);
    }

    public boolean isEmptyResource() {
        return resource.isEmpty();
    }

    public String getResource() {
        return resource;
    }
}
