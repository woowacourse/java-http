package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ResponseEntity {

    public static final String STATIC_RESOURCE_DIRECTORY = "static";

    private final HttpStatus httpStatus;
    private final HttpCookie httpCookie;
    private final ContentType contentType;
    private final String content;

    public ResponseEntity(
            final HttpStatus httpStatus,
            final HttpCookie httpCookie,
            final ContentType contentType,
            final String content
    ) {
        this.httpStatus = httpStatus;
        this.httpCookie = httpCookie;
        this.contentType = contentType;
        this.content = content;
    }

    public ResponseEntity(
            final HttpStatus httpStatus,
            final ContentType contentType,
            final String content
    ) {
        this.httpStatus = httpStatus;
        this.httpCookie = new HttpCookie(new HashMap<>());
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseEntity of(final HttpStatus httpStatus, final String resourcePath) {
        try {
            final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath);

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

    private static ContentType calculateContentType(final URL resourceFileUrl) {
        if (resourceFileUrl.toString().endsWith(".css")) {
            return ContentType.TEXT_CSS;
        }

        return ContentType.TEXT_HTML;
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

    public ContentType getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
