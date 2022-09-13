package org.apache.coyote.http11.handler;

import java.util.HashMap;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class HandlerResponseEntity {

    private static final String EMPTY_BODY = "";

    private final HttpStatus httpStatus;
    private final HttpResponseHeader httpResponseHeader;
    private final String body;
    private final String resource;

    public HandlerResponseEntity(final HttpStatus httpStatus, final HttpResponseHeader headers, final String body,
                                 final String resource) {
        this.httpStatus = httpStatus;
        this.httpResponseHeader = headers;
        this.body = body;
        this.resource = resource;
    }

    public static HandlerResponseEntity createWithResource(final HttpResponseHeader headers, final String resource) {
        return new HandlerResponseEntity(HttpStatus.OK, headers, EMPTY_BODY, resource);
    }

    public static HandlerResponseEntity createWithResource(final String resource) {
        return createWithResource(new HttpResponseHeader(new HashMap<>()), resource);
    }

    public static HandlerResponseEntity createRedirectResponse(final HttpStatus httpStatus,
                                                               final HttpResponseHeader headers, final String path) {
        headers.addHeader("Location", path);
        return createResponseBody(httpStatus, headers, EMPTY_BODY);
    }

    public static HandlerResponseEntity createResponseBody(final HttpStatus httpStatus,
                                                           final HttpResponseHeader headers, final String body) {
        return new HandlerResponseEntity(httpStatus, headers, body, EMPTY_BODY);
    }

    public boolean isEmptyResource() {
        return resource.isEmpty();
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

    public String getResource() {
        return resource;
    }
}
