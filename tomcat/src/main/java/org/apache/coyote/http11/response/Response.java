package org.apache.coyote.http11.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    private static final Logger log = LoggerFactory.getLogger(Response.class);

    //    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;
    private final HttpStatus httpStatus;

    private Response(final ResponseBody responseBody, final HttpStatus httpStatus) {
//        this.responseHeader = parseResponseHeader(responseBody);
        this.responseBody = responseBody;
        this.httpStatus = httpStatus;
    }

    public static Response of(final ResponseBody responseBody, final HttpStatus httpStatus) {
        return new Response(responseBody, httpStatus);
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + responseBody.getContentType().getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getContent().getBytes().length + " ",
                "",
                responseBody.getContent());
    }
}


