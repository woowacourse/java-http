package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final HttpHeader httpHeader;
    private final String body;

    public ResponseEntity(final HttpStatus httpStatus, final HttpHeader httpHeader, final String body) {
        this.httpStatus = httpStatus;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public static ResponseEntity of(final ServletResponseEntity response) {
        return new ResponseEntity(response.getHttpStatus(), response.getHttpHeader(), response.getBody());
    }

    public static ResponseEntity createHtmlResponse(final ServletResponseEntity response) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");

        return new ResponseEntity(HttpStatus.OK, new HttpHeader(headers), response.getResource());
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public String getBody() {
        return body;
    }
}
