package org.apache.coyote.http11.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    private static final Logger log = LoggerFactory.getLogger(Response.class);

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public Response(final StatusLine statusLine, final ResponseHeader responseHeader, final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }


    public static Response of(final ResponseBody responseBody, final HttpStatus httpStatus) {
        return new Response(StatusLine.of(httpStatus), ResponseHeader.basic(responseBody), responseBody);
    }

    public static Response redirect(final String redirectPath, final ResponseBody responseBody, final HttpStatus httpStatus) {
        return new Response(StatusLine.of(httpStatus), ResponseHeader.redirect(responseBody, redirectPath), responseBody);
    }

    @Override
    public String toString() {
        final String result = statusLine.getStatusLine() + "\r\n" +
                responseHeader.toString() + "\r\n" +
                "" + "\r\n" +
                responseBody.getContent();
        log.info("result = {}", result);
        return result;
    }
}


