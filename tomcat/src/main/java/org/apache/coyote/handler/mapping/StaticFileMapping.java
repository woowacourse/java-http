package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class StaticFileMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() &&
                (httpRequest.getRequestUri().getRequestUri().endsWith(".html") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".js") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".css") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".ico")
                );
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        final String filePath = "static" + requestUri;

        if (requestUri.endsWith(".html")) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(CONTENT_TYPE, ContentType.HTML.getValue())
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".js")) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(CONTENT_TYPE, ContentType.JS.getValue())
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".css")) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(CONTENT_TYPE, ContentType.CSS.getValue())
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".ico")) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(CONTENT_TYPE, ContentType.ICO.getValue())
                    .body(HttpBody.file(filePath))
                    .build();
        }

        return HttpResponse.redirect("/404.html");
    }
}
