package org.apache.coyote.http11.handler;

import java.util.HashMap;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class ServletResponseEntity extends ResponseEntity {

    private final String resource;

    public ServletResponseEntity(final HttpStatus httpStatus, final HttpResponseHeader httpResponseHeader, final String body,
                                 final String resource) {
        super(httpStatus, httpResponseHeader, body);
        this.resource = resource;
    }

    public static ServletResponseEntity createResponseBody(final HttpStatus httpStatus, final HttpResponseHeader headers,
                                                           final String body) {
        return new ServletResponseEntity(httpStatus, headers, body, null);
    }

    public static ServletResponseEntity createWithResource(final HttpResponseHeader httpResponseHeader, final String resource) {
        return new ServletResponseEntity(HttpStatus.OK, httpResponseHeader, null, resource);
    }

    public static ServletResponseEntity createWithResource(final String resource) {
        return createWithResource(new HttpResponseHeader(new HashMap<>()), resource);
    }

    public String getResource() {
        return resource;
    }
}
