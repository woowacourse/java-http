package org.apache.coyote.http11.response;

import java.io.IOException;
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

    public static Http11Response extract(final Url url, String httpMethod) throws IOException {
        if (url.isEmpty()) {
            return new Http11Response(ContentType.from(url.getPath()), HttpStatus.OK, "Hello world!");
        }
        String resource = IOUtils.readResourceFile(url, httpMethod);
        return new Http11Response(ContentType.from(url.getPath()), url.getResponse(httpMethod).getHttpStatus(), resource);
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
