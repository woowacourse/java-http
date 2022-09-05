package org.apache.coyote.http11.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.*;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.LinkedHashMap;

class FileHandlerTest {

    @Test
    void handle() throws IOException {
        // given
        final HttpStartLine httpStartLine = HttpStartLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.HOST, "localhost:8080");
        httpHeaders.put(HttpHeader.CONNECTION, "keep-alive");
        httpHeaders.put(HttpHeader.ACCEPT, "text/html");
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");

        final HttpHeaders responseHeaders = new HttpHeaders(new LinkedHashMap<>());
        responseHeaders.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        responseHeaders.put(HttpHeader.CONTENT_LENGTH, "5564");
        final HttpResponse expected =
                new HttpResponse(HttpStatus.OK, responseHeaders, ResourceLoader.getContent("index.html"));

        // when
        final HttpResponse actual = new FileHandler().handle(httpRequest);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("httpHeaders")
                .isEqualTo(expected);
    }
}
