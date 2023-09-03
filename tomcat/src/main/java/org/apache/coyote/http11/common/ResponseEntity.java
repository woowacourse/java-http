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
        final ResponseBody responseBody = ResponseBody.from(requestURI);

        return String.join(
                CRLF,
                parseHttpStatusLine(),
                "Content-Type: text/html;charset=utf-8 ",
                parseContentLengthLine(responseBody),
                EMPTY,
                responseBody.body()
        );
    }

    private String parseHttpStatusLine() {
        return String.join(SPACE, requestURI.getHttpVersion(), "200", "OK", "");
    }

    private String parseContentLengthLine(final ResponseBody responseBody) {
        return String.join(SPACE, "Content-Length:", String.valueOf(responseBody.contentLength()), "");
    }

}
