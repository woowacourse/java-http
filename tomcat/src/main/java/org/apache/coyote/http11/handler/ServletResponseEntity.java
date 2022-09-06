package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class ServletResponseEntity extends ResponseEntity {

    private final String resource;

    public ServletResponseEntity(final HttpStatus httpStatus, final HttpHeader httpHeader, final String body, final String resource) {
        super(httpStatus, httpHeader, body);
        this.resource = resource;
    }

    public static ServletResponseEntity createResponseBody(final HttpStatus httpStatus, final Map<String, String> headers, final String body) {
        return new ServletResponseEntity(httpStatus, new HttpHeader(headers), body, null);
    }

    public static ServletResponseEntity createWithResource(final HttpHeader httpHeader, final String resource) {
        return new ServletResponseEntity(HttpStatus.OK, httpHeader, null, resource);
    }

    public static ServletResponseEntity createWithResource(final String resource) {
        return createWithResource(new HttpHeader(new HashMap<>()), resource);
    }

    public String getResource() {
        return resource;
    }
}
