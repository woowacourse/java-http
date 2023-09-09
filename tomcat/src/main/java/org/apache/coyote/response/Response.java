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

    public void setProtocol(Protocol protocol) {
        responseHeader.setProtocol(protocol);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        responseHeader.setHttpStatus(httpStatus);
    }

    public void addHeaders(String key, String value) {
        responseHeader.addHeaders(key, value);
    }

    public void addCookie(String key, String value) {
        responseHeader.addCookie(key, value);
    }

    public void redirectLocation(String path) {
        addHeaders(LOCATION, path);
    }

    public void setResponseBody(String body) {
        responseBody.setBody(body);
    }

    public byte[] getResponseBytes() {
        return String.join("\r\n", responseHeader.getHeader(), responseBody.getBody()).getBytes();
    }
}
