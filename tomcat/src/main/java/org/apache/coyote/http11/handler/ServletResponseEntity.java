package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class ServletResponseEntity extends ResponseEntity {

    private final String resource;

    public ServletResponseEntity(final String resource) {
        super(HttpStatus.OK, "text/html", null);
        this.resource = resource;
    }

    public ServletResponseEntity(final HttpStatus httpStatus, final String mimeType, final String body) {
        super(httpStatus, mimeType, body);
        this.resource = null;
    }

    public String getResource() {
        return resource;
    }
}
