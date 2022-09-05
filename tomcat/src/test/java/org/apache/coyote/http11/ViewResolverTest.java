package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @DisplayName("정적 파일을 읽어와 HttpResponse를 반환한다.")
    @Test
    void staticFileRequest() throws IOException {
        String fileName = "/js/scripts.js";
        HttpResponse httpResponse = ViewResolver.staticFileRequest(fileName);

        URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_TYPE)).isEqualTo(
                        ContentType.JS.getContentType()),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_LENGTH)).isEqualTo(
                        String.valueOf(expectedResponseBody.getBytes().length)),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(expectedResponseBody)
        );
    }

    @DisplayName("존재하지 않는 정적 파일을 요청할 경우 Not Found 응답을 한다.")
    @Test
    void responseBadRequestWhenRequestInvalidStaticFile() throws IOException {
        String fileName = "/rex/rex.js";
        HttpResponse httpResponse = ViewResolver.staticFileRequest(fileName);
        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }
}
