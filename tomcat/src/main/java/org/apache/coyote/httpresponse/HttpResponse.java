package org.apache.coyote.httpresponse;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.QueryString;
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

    public static HttpResponse init(final String httpVersion) {
        return new HttpResponse(httpVersion, null, null, null);
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        return new HttpResponse(this.httpVersion, httpStatus, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setContent(final String path) {
        final String contentBody = ResourceReader.read(path);
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        return new HttpResponse(this.httpVersion, this.httpStatus, responseHeaders, contentBody);
    }

    public HttpResponse setContent(final String path, final QueryString queryString) {
        final String contentBody = ResourceReader.read(path);
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        return new HttpResponse(this.httpVersion, this.httpStatus, responseHeaders, contentBody);
    }

    public HttpResponse setLocationHeader(final String path) {
        responseHeaders.setLocationHeader(path);
        return new HttpResponse(this.httpVersion, HttpStatus.FOUND, responseHeaders, this.contentBody);
    }

    public byte[] getBytes() {
        final String responseLine = makeResponseLine();
        final String responseHeaders = this.responseHeaders.getFormattedHeaders();
        return String.join("\r\n", responseLine, responseHeaders, contentBody)
                .getBytes(StandardCharsets.UTF_8);
    }

    private String makeResponseLine() {
        return httpVersion + DELIMITER + httpStatus.getHttpStatus() + " ";
    }
}
