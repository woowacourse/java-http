package org.apache.coyote.httpresponse;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.header.ResourceReader;
import org.apache.coyote.httpresponse.header.ResponseHeaders;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String DELIMITER = " ";

    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final ResponseHeaders responseHeaders;
    private final String contentBody;

    public HttpResponse(final String httpVersion, final HttpStatus httpStatus, final ResponseHeaders responseHeaders, final String contentBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.contentBody = contentBody;
    }

    public static HttpResponse from(final HttpRequest request) throws URISyntaxException {
        final String httpVersion = request.getHttpVersion();
        final String path = request.getRequestUri();
        final String contentBody = ResourceReader.read(path);
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        return new HttpResponse(httpVersion, HttpStatus.OK, responseHeaders, contentBody);
    }

    private String makeResponseLine() {
        return httpVersion + DELIMITER + httpStatus.getHttpStatus() + " ";
    }

    public byte[] getBytes() {
        final String responseLine = makeResponseLine();
        final String responseHeaders = this.responseHeaders.getFormattedHeaders();
        return String.join("\r\n", responseLine, responseHeaders, contentBody)
                .getBytes(StandardCharsets.UTF_8);
    }
}
