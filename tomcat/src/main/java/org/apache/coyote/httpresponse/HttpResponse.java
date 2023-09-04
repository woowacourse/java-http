package org.apache.coyote.httpresponse;

import org.apache.coyote.httprequest.QueryString;
import org.apache.coyote.httpresponse.header.ResourceReader;
import org.apache.coyote.httpresponse.header.ResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

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
        log.debug("===========================================");
        log.debug("Http Response");
        log.debug("Response Http Version: " + httpVersion);
        return new HttpResponse(httpVersion, null, null, null);
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        log.debug("Http Status: " + httpStatus);
        return new HttpResponse(this.httpVersion, httpStatus, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setContent(final String path) {
        final String contentBody = ResourceReader.read(path);
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        log.debug("Content-Path: " + path);
        return new HttpResponse(this.httpVersion, this.httpStatus, responseHeaders, contentBody);
    }

    public HttpResponse setBlankContent() {
        final ResponseHeaders responseHeaders = ResponseHeaders.init();
        return new HttpResponse(this.httpVersion, this.httpStatus, responseHeaders, "");
    }


    public HttpResponse setContent(final String path, final QueryString queryString) {
        final String contentBody = ResourceReader.read(path);
        final ResponseHeaders responseHeaders = ResponseHeaders.of(path, contentBody);
        log.debug("Content-Path: " + path);
        return new HttpResponse(this.httpVersion, this.httpStatus, responseHeaders, contentBody);
    }

    public HttpResponse setLocationHeader(final String path) {
        responseHeaders.setLocationHeader(path);
        log.debug("Location: " + path);
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
