package org.apache.coyote.httpresponse;

import org.apache.coyote.http11.common.CookieHeader;
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
    private final CookieHeader cookieHeader;
    private final ResponseHeaders responseHeaders;
    private final ContentBody contentBody;

    public HttpResponse(
            final String httpVersion,
            final HttpStatus httpStatus,
            final CookieHeader cookieHeader,
            final ResponseHeaders responseHeaders,
            final ContentBody contentBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.cookieHeader = cookieHeader;
        this.responseHeaders = responseHeaders;
        this.contentBody = contentBody;
    }

    public static HttpResponse init(final String httpVersion) {
        log.debug("======================================================================");
        log.debug("Http Response");
        log.debug("Response Http Version: {}", httpVersion);
        return new HttpResponse(httpVersion, null, CookieHeader.blank(), null, null);
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        log.debug("Http Status: {}", httpStatus);
        return new HttpResponse(this.httpVersion, httpStatus, this.cookieHeader, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setCookieHeader(final CookieHeader cookieHeader) {
        return new HttpResponse(this.httpVersion, httpStatus, cookieHeader, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setContent(final String path) {
        final String content = ResourceReader.read(path);
        final ContentBody newContentBody = new ContentBody(content);
        final ResponseHeaders newResponseHeaders = ResponseHeaders.of(path, newContentBody);
        log.debug("Content-Path: {}", path);
        return new HttpResponse(this.httpVersion, this.httpStatus, this.cookieHeader, newResponseHeaders, newContentBody);
    }

    public HttpResponse setBlankContent() {
        final ResponseHeaders blankResponseHeader = ResponseHeaders.init();
        final ContentBody blankContentBody = ContentBody.noContent();
        return new HttpResponse(this.httpVersion, this.httpStatus, this.cookieHeader, blankResponseHeader, blankContentBody);
    }

    public HttpResponse setLocationHeader(final String path) {
        responseHeaders.setLocationHeader(path);
        log.debug("Location: {}", path);
        return new HttpResponse(this.httpVersion, HttpStatus.FOUND, this.cookieHeader, responseHeaders, this.contentBody);
    }

    public byte[] getBytes() {
        final StringBuilder stringBuilder = new StringBuilder();
        final String responseLine = makeResponseLine();
        stringBuilder.append(responseLine).append("\r\n");
        if (cookieHeader.isExist()) {
            stringBuilder.append(cookieHeader.getFormattedValue()).append("\r\n");
        }
        stringBuilder.append(responseHeaders.getFormattedHeaders()).append("\r\n");
        stringBuilder.append(contentBody.getValue());
        return stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String makeResponseLine() {
        return httpVersion + DELIMITER + httpStatus.getHttpStatus() + DELIMITER;
    }
}
