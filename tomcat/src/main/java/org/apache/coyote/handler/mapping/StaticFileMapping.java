package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

import static org.apache.coyote.http.common.ContentType.CSS;
import static org.apache.coyote.http.common.ContentType.HTML;
import static org.apache.coyote.http.common.ContentType.ICO;
import static org.apache.coyote.http.common.ContentType.JS;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class StaticFileMapping implements HandlerMapping {

    private static final String DEFAULT_FILE_PATH = "static";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && isStaticFile(httpRequest);
    }

    private static boolean isStaticFile(final HttpRequest httpRequest) {
        return httpRequest.isRequestUriEndsWith(HTML.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(JS.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(CSS.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(ICO.getFileExtension());
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        final String filePath = DEFAULT_FILE_PATH + requestUri;

        if (httpRequest.isRequestUriEndsWith(HTML.getFileExtension())) {
            return createHttpResponseByContentTypeAndPath(HTML, filePath);
        }

        if (httpRequest.isRequestUriEndsWith(JS.getFileExtension())) {
            return createHttpResponseByContentTypeAndPath(JS, filePath);
        }

        if (httpRequest.isRequestUriEndsWith(CSS.getFileExtension())) {
            return createHttpResponseByContentTypeAndPath(CSS, filePath);
        }

        if (httpRequest.isRequestUriEndsWith(ICO.getFileExtension())) {
            return createHttpResponseByContentTypeAndPath(ICO, filePath);
        }

        return HttpResponse.redirect("/404.html");
    }

    private static HttpResponse createHttpResponseByContentTypeAndPath(final ContentType contentType, final String filePath) throws IOException {
        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.OK))
                .httpHeaders(CONTENT_TYPE, contentType.getValue())
                .body(HttpBody.file(filePath))
                .build();
    }
}
