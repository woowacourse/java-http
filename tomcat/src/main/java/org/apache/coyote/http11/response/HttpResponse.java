package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";

    private ResponseStatusLine responseStatusLine;
    private HttpHeaders headers;
    private String responseBody;

    public HttpResponse(final ResponseStatusLine responseStatusLine, final HttpHeaders headers,
                        final String responseBody) {
        this.responseStatusLine = responseStatusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse create() {
        final ResponseStatusLine statusLine = ResponseStatusLine.create(HttpStatus.OK);
        final HttpHeaders httpHeaders = HttpHeaders.create();

        return new HttpResponse(statusLine, httpHeaders, "");
    }

    public void setStatusLine(final HttpStatus httpStatus) {
        this.responseStatusLine = ResponseStatusLine.create(httpStatus);
    }

    public void setHeaders(final Path path) throws IOException {
        this.headers = HttpHeaders.createResponse(path);
    }

    public void setLocation(final String redirectUri) {
        this.headers.setHeader(HttpHeaders.LOCATION, redirectUri);
    }

    public void setCookie(final String key, final String value) {
        this.headers.setCookie(key, value);
    }

    public void setHeadersSimpleText() {
        this.headers = HttpHeaders.createSimpleText();
    }

    public void setResponseBody(final String body) {
        this.responseBody = body;
    }

    @Override
    public String toString() {
        return responseStatusLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
