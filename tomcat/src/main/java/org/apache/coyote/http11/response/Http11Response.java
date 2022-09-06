package org.apache.coyote.http11.response;

import java.io.IOException;

public class Http11Response {

    private final String contentType;
    private final HttpStatus httpStatus;
    private final String resource;

    public Http11Response(String path, HttpStatus httpStatus, String resource) {
        this.contentType = ContentType.from(path);
        this.httpStatus = httpStatus;
        this.resource = resource;
    }

    public String toResponse() throws IOException {
        return createResponse(httpStatus, contentType, resource);
    }

    private String createResponse(HttpStatus httpStatus, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getName() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String getResource() {
        return resource;
    }
}
