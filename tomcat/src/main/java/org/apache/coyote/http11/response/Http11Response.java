package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.url.Url;

public class Http11Response {
    private static final String STATIC_DIRECTORY = "static/";

    private final String contentType;
    private final HttpStatus httpStatus;
    private final String resource;

    public static Http11Response create(String uri) {
        return new Http11Response(ContentType.from(uri), null, null);
    }

    public Http11Response(String contentType, HttpStatus httpStatus, String resource) {
        this.contentType = contentType;
        this.httpStatus = httpStatus;
        this.resource = resource;
    }

    public Http11Response extract(final Url url) throws IOException {
        if (url.getPath().isEmpty()) {
            return new Http11Response(contentType, HttpStatus.OK, "Hello world!");
        }
        URL resource = this.getClass()
                .getClassLoader()
                .getResource(STATIC_DIRECTORY + url.getResponse().getResource());

        validatePath(resource);
        String response = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return new Http11Response(contentType, url.getResponse().getHttpStatus(), response);
    }

    private void validatePath(URL resource) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("경로가 잘못 되었습니다. : null");
        }
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
