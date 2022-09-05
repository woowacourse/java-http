package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.url.Url;
import org.apache.coyote.http11.utils.IOUtils;

public class Http11Response {

    private final String contentType;
    private final HttpStatus httpStatus;
    private final String resource;

    public Http11Response(String contentType, HttpStatus httpStatus, String resource) {
        this.contentType = contentType;
        this.httpStatus = httpStatus;
        this.resource = resource;
    }

    public static Http11Response extract(final Url url) throws IOException {
        if (url.getPath().isEmpty()) {
            return new Http11Response(ContentType.from(url.getPath()), HttpStatus.OK, "Hello world!");
        }
        String resource = IOUtils.readResourceFile(url);
        return new Http11Response(ContentType.from(url.getPath()), url.getResource().getHttpStatus(), resource);
    }

    public String getContentType() {
        return contentType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getResource() {
        return resource;
    }
}
