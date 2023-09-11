package org.apache.coyote.httpresponse;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.controller.util.ResourceReader;
import org.apache.coyote.httpresponse.header.ResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static final String DELIMITER = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private CookieResponseHeader cookieResponseHeader;
    private ResponseHeaders responseHeaders;
    private ContentBody contentBody;

    public HttpResponse(
            final HttpVersion httpVersion,
            HttpStatus httpStatus,
            CookieResponseHeader cookieResponseHeader,
            ResponseHeaders responseHeaders,
            ContentBody contentBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.cookieResponseHeader = cookieResponseHeader;
        this.responseHeaders = responseHeaders;
        this.contentBody = contentBody;
    }

    public static HttpResponse init(final HttpVersion httpVersion) {
        log.debug("======================================================================");
        log.debug("Http Response");
        log.debug("Response Http Version: {}", httpVersion.getValue());
        return new HttpResponse(
                httpVersion,
                HttpStatus.INTERNAL_SERVER_ERROR,
                CookieResponseHeader.blank(),
                ResponseHeaders.blank(),
                ContentBody.noContent());
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        log.debug("Http Status: {}", httpStatus);
    }

    public void setCookieHeader(final CookieResponseHeader cookieResponseHeader) {
        this.cookieResponseHeader = cookieResponseHeader;
    }

    public void setContent(final String path) {
        final String content = ResourceReader.read(path);
        final ContentBody newContentBody = new ContentBody(content);
        this.contentBody = newContentBody;
        this.responseHeaders =  ResponseHeaders.of(path, newContentBody);;
        log.debug("Content-Path: {}", path);
    }

    public void setLocationHeader(final String path) {
        responseHeaders.setLocationHeader(path);
        this.httpStatus = HttpStatus.FOUND;
        log.debug("Location: {}", path);
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
        return httpVersion.getValue() + DELIMITER + httpStatus.getHttpStatus() + DELIMITER;
    }
}
