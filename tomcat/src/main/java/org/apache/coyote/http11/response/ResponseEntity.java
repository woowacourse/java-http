package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.requestUri.ResourcePath;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class ResponseEntity {

    public static final String STATIC_RESOURCE_DIRECTORY = "static";

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String content;

    private ResponseEntity(final HttpStatus httpStatus, final ContentType contentType, final String content) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseEntity from(final HttpRequest httpRequest) throws URISyntaxException, IOException {
        final ResourcePath resourcePath = httpRequest.getRequestLine().getRequestUri().getResourcePath();
        if (resourcePath.isRootPath()) {
            return new ResponseEntity(HttpStatus.OK, ContentType.TEXT_HTML, "Hello world!");
        }

        final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath.getResourcePath());
        final File resourceFile = new File(resourceFileUrl.getFile());

        return new ResponseEntity(
                HttpStatus.OK,
                calculateContentType(resourceFileUrl),
                new String(Files.readAllBytes(resourceFile.toPath()))
        );
    }

    private static ContentType calculateContentType(final URL resourceFileUrl) {
        if (resourceFileUrl.toString().endsWith(".css")) {
            return ContentType.TEXT_CSS;
        }

        return ContentType.TEXT_HTML;
    }

    public int calculateContentLength() {
        return content.getBytes().length;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
