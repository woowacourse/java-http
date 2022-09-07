package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final HttpResponseHeader httpResponseHeader;
    private final String body;

    public ResponseEntity(final HttpStatus httpStatus, final HttpResponseHeader httpResponseHeader, final String body) {
        this.httpStatus = httpStatus;
        this.httpResponseHeader = httpResponseHeader;
        this.body = body;
    }

    public static ResponseEntity of(final ServletResponseEntity response) {
        return new ResponseEntity(response.getHttpStatus(), response.getHttpHeader(), response.getBody());
    }

    public static ResponseEntity createTextHtmlResponse(final ServletResponseEntity response) {
        final HttpResponseHeader headers = response.getHttpHeader();
        headers.addHeader("Content-Type", "text/html");

        return new ResponseEntity(HttpStatus.OK, headers, response.getResource());
    }

    public static ResponseEntity createErrorRedirectResponse(final HttpStatus httpStatus, final String location) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);

        return new ResponseEntity(httpStatus, new HttpResponseHeader(headers), "");
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpResponseHeader getHttpHeader() {
        return httpResponseHeader;
    }

    public String getBody() {
        return body;
    }
}
