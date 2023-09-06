package org.apache.coyote.httpresponse;

import org.apache.coyote.httpresponse.header.ResourceReader;
import org.apache.coyote.httpresponse.header.ResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static final String DELIMITER = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final CookieResponseHeader cookieResponseHeader;
    private final ResponseHeaders responseHeaders;
    private final ContentBody contentBody;

    public HttpResponse(
            final String httpVersion,
            final HttpStatus httpStatus,
            final CookieResponseHeader cookieResponseHeader,
            final ResponseHeaders responseHeaders,
            final ContentBody contentBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.cookieResponseHeader = cookieResponseHeader;
        this.responseHeaders = responseHeaders;
        this.contentBody = contentBody;
    }

    public static HttpResponse init(final String httpVersion) {
        log.debug("======================================================================");
        log.debug("Http Response");
        log.debug("Response Http Version: {}", httpVersion);
        return new HttpResponse(httpVersion, null, CookieResponseHeader.blank(), null, null);
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        log.debug("Http Status: {}", httpStatus);
        return new HttpResponse(this.httpVersion, httpStatus, this.cookieResponseHeader, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setCookieHeader(final CookieResponseHeader cookieResponseHeader) {
        return new HttpResponse(this.httpVersion, httpStatus, cookieResponseHeader, this.responseHeaders, this.contentBody);
    }

    public HttpResponse setContent(final String path) {
        final String content = ResourceReader.read(path);
        final ContentBody newContentBody = new ContentBody(content);
        final ResponseHeaders newResponseHeaders = ResponseHeaders.of(path, newContentBody);
        log.debug("Content-Path: {}", path);
        return new HttpResponse(this.httpVersion, this.httpStatus, this.cookieResponseHeader, newResponseHeaders, newContentBody);
    }

    public HttpResponse setBlankContent() {
        final ResponseHeaders blankResponseHeader = ResponseHeaders.init();
        final ContentBody blankContentBody = ContentBody.noContent();
        return new HttpResponse(this.httpVersion, this.httpStatus, this.cookieResponseHeader, blankResponseHeader, blankContentBody);
    }

    public HttpResponse setLocationHeader(final String path) {
        responseHeaders.setLocationHeader(path);
        log.debug("Location: {}", path);
        return new HttpResponse(this.httpVersion, HttpStatus.FOUND, this.cookieResponseHeader, responseHeaders, this.contentBody);
    }

    public byte[] getBytes() {
        final StringBuilder stringBuilder = new StringBuilder();
        final String responseLine = makeResponseLine();
        stringBuilder.append(responseLine).append(LINE_SEPARATOR);
        if (cookieResponseHeader.isExist()) {
            stringBuilder.append(cookieResponseHeader.getFormattedValue()).append(LINE_SEPARATOR);
        }
        stringBuilder.append(responseHeaders.getFormattedHeaders()).append(LINE_SEPARATOR);
        stringBuilder.append(contentBody.getValue());
        return stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String makeResponseLine() {
        return httpVersion + DELIMITER + httpStatus.getHttpStatus() + DELIMITER;
    }
}
