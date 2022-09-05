package org.apache.coyote.http11.web.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;

class HttpResponseTest {

    @DisplayName("HTTP 응답 포매팅이 제대로 이루어지는지 확인한다.")
    @Test
    void format() throws IOException {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.CONTENT_TYPE, "*/*");

        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("index.html"));

        // when
        final String formattedResponse = httpResponse.format();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: */* \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(formattedResponse).isEqualTo(expected);
    }
}
