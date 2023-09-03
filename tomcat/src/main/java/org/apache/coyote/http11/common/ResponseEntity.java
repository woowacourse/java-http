package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.IOException;

public class ResponseEntity {

    private final RequestURI requestURI;

    private ResponseEntity(final RequestURI requestURI) {
        this.requestURI = requestURI;
    }

    public static ResponseEntity from(final RequestURI requestURI) {
        return new ResponseEntity(requestURI);
    }

    public String getResponse() throws IOException {
        final var uri = requestURI.getUri();
        final var responseHeader = ResponseHeader.from(requestURI);
        final var responseBody = ResponseBody.from(requestURI);

        return String.join(
                CRLF,
                parseHttpStatusLine(responseHeader),
                parseContentTypeLine(uri),
                parseContentLengthLine(responseBody),
                EMPTY,
                responseBody.body()
        );
    }

    private String parseHttpStatusLine(final ResponseHeader responseHeader) {
        final HttpStatus httpStatus = responseHeader.getHttpStatus();
        return String.join(
                SPACE,
                requestURI.getHttpVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.name(),
                ""
        );
    }

    private String parseContentTypeLine(final String uri) {
        if (uri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }

        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String parseContentLengthLine(final ResponseBody responseBody) {
        return String.join(SPACE, "Content-Length:", String.valueOf(responseBody.contentLength()), "");
    }

}
