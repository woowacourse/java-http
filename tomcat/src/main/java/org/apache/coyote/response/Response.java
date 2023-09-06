package org.apache.coyote.response;

public class Response {

    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public Response(ResponseHeader responseHeader, ResponseBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public String getResponse() {
        return String.join("\r\n", responseHeader.getHeader(), " ", responseBody.getBody());
    }
}
