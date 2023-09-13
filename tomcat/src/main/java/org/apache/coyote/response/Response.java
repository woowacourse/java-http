package org.apache.coyote.response;

import org.apache.coyote.http11.Protocol;

public class Response {

    private static final String LOCATION = "Location";

    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public Response() {
        responseHeader = new ResponseHeader();
        responseBody = new ResponseBody();
    }

    public Response(ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public Response setProtocol(Protocol protocol) {
        responseHeader.setProtocol(protocol);
        return this;
    }

    public Response setHttpStatus(HttpStatus httpStatus) {
        responseHeader.setHttpStatus(httpStatus);
        return this;
    }

    public Response addHeaders(String key, String value) {
        responseHeader.addHeaders(key, value);
        return this;
    }

    public Response addCookie(String key, String value) {
        responseHeader.addCookie(key, value);
        return this;
    }

    public Response redirectLocation(String path) {
        addHeaders(LOCATION, path);
        return this;
    }

    public Response setResponseBody(String body) {
        responseBody.setBody(body);
        return this;
    }

    public byte[] getResponseBytes() {
        return String.join("\r\n", responseHeader.getHeader(), responseBody.getBody()).getBytes();
    }
}
