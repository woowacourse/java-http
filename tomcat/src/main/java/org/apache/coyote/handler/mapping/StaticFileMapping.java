package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;
import java.util.Map;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class StaticFileMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() &&
                ("/".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        "/index.html".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".js") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".css") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".ico")
                );
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        if ("/".equals(requestUri)) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.TEXT.getValue())))
                    .body(new HttpBody("Hello world!"))
                    .build();
        }

        if ("/index.html".equals(requestUri)) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.HTML.getValue())))
                    .body(HttpBody.file("static/index.html"))
                    .build();
        }

        if (requestUri.endsWith(".js")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.JS.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".css")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.CSS.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".ico")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.ICO.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        return null;
    }
}
