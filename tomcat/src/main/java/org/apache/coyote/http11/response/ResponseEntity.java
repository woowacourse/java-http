package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.header.ContentTypeValue;
import org.apache.coyote.http11.common.header.HttpCookie;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseEntity {

    public static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String BAD_REQUEST_FILE = "/404.html";

    private final HttpStatus httpStatus;
    private final HttpCookie httpCookie;
    private final ContentTypeValue contentTypeValue;
    private final String content;

    public ResponseEntity(
            final HttpStatus httpStatus,
            final HttpCookie httpCookie,
            final ContentTypeValue contentTypeValue,
            final String content
    ) {
        this.httpStatus = httpStatus;
        this.httpCookie = httpCookie;
        this.contentTypeValue = contentTypeValue;
        this.content = content;
    }

    public ResponseEntity(
            final HttpStatus httpStatus,
            final ContentTypeValue contentTypeValue,
            final String content
    ) {
        this.httpStatus = httpStatus;
        this.httpCookie = null;
        this.contentTypeValue = contentTypeValue;
        this.content = content;
    }

    public static ResponseEntity of(final HttpStatus httpStatus, final String resourcePath) {
        try {
            final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath);

            if (resourceFileUrl == null) {
                return ResponseEntity.of(HttpStatus.BAD_REQUEST, BAD_REQUEST_FILE);
            }

            return new ResponseEntity(
                    httpStatus,
                    HttpCookie.from(""),
                    calculateContentType(resourceFileUrl),
                    new String(Files.readAllBytes(Path.of(resourceFileUrl.toURI())))
            );
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static ContentTypeValue calculateContentType(final URL resourceFileUrl) {
        if (resourceFileUrl.toString().endsWith(".css")) {
            return ContentTypeValue.TEXT_CSS;
        }

        return ContentTypeValue.TEXT_HTML;
    }

    public static ResponseEntity of(final HttpStatus httpStatus, final HttpCookie httpCookie, final String resourcePath) {
        try {
            final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath);

            return new ResponseEntity(
                    httpStatus,
                    httpCookie,
                    calculateContentType(resourceFileUrl),
                    new String(Files.readAllBytes(Path.of(resourceFileUrl.toURI())))
            );
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public int calculateContentLength() {
        return content.getBytes().length;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public ContentTypeValue getContentType() {
        return contentTypeValue;
    }

    public String getContent() {
        return content;
    }
}
