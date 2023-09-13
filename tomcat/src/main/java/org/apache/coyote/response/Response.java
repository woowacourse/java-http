package org.apache.coyote.response;

import org.apache.coyote.http11.Protocol;

public class Response {

    private static final String LOCATION = "Location";

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public Response(Protocol protocol) {
        this(new ResponseLine(protocol), new ResponseHeader(), new ResponseBody());
    }

    public Response(ResponseLine responseLine, ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public Response setProtocol(Protocol protocol) {
        responseLine.setProtocol(protocol);
        return this;
    }

    public Response setHttpStatus(HttpStatus httpStatus) {
        responseLine.setHttpStatus(httpStatus);
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
        return String.join("\r\n", responseLine.getResponseLine(), responseHeader.getHeader(), responseBody.getBody()).getBytes();
    }
}
