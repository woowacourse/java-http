package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.url.Url;

public class Http11Response {

    private final String contentType;
    private final HttpStatus httpStatus;
    private final String resource;

    public Http11Response(String path, HttpStatus httpStatus, String resource) {
        this.contentType = ContentType.from(path);
        this.httpStatus = httpStatus;
        this.resource = resource;
    }

    public static Http11Response extract(final Url url, final HttpHeaders httpHeaders,
                                         final String requestBody) throws IOException {
        if (url.getHttpMethod().equals(HttpMethod.GET.name())) {
            return url.getResponse(httpHeaders);
        }
        return url.postResponse(httpHeaders, requestBody);
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
