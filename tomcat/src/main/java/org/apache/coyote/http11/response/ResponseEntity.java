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

    public static ResponseEntityBuilder builder() {
        return new ResponseEntityBuilder();
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

    public static class ResponseEntityBuilder {

        private HttpStatus httpStatus;
        private HttpCookie httpCookie;
        private String resourcePath;

        public ResponseEntityBuilder httpStatus(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            if (this.resourcePath == null) {
                this.resourcePath(httpStatus.getResourcePath());
            }

            return this;
        }

        public ResponseEntityBuilder httpCookie(final HttpCookie httpCookie) {
            this.httpCookie = httpCookie;
            return this;
        }

        public ResponseEntityBuilder resourcePath(final String resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public ResponseEntity build() {
            try {
                final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath);

                final ContentTypeValue contentTypeValue = calculateContentType(resourceFileUrl);
                final String content = new String(Files.readAllBytes(Path.of(resourceFileUrl.toURI())));

                return new ResponseEntity(httpStatus, httpCookie, contentTypeValue, content);
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
    }
}
