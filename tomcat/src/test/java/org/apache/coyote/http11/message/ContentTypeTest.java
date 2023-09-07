package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.exception.UnsupportedContentTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {
    
    @Test
    @DisplayName("Accept Type에 맞는 ContentType을 찾아온다.")
    void findResponseContentTypeFromRequest() throws IOException {
       // given
        final HttpRequest httpRequest = createHttpRequest("/styles.css", "text/css");

        // when
        final ContentType contentType = ContentType.findResponseContentTypeFromRequest(httpRequest);

        // then
        assertThat(contentType).isEqualTo(ContentType.CSS);
    }

    @Test
    @DisplayName("Accept Type이 비어있다면 요청 URI를 통해 ContentType을 찾아온다.")
    void findResponseContentType_empty() throws IOException {
        // given
        final HttpRequest httpRequest = createHttpRequest("/styles.css", null);

        // when
        final ContentType contentType = ContentType.findResponseContentTypeFromRequest(httpRequest);

        // then
        assertThat(contentType).isEqualTo(ContentType.CSS);
    }

    @Test
    @DisplayName("Accept Type이 */* 이라면 요청 URI의 파일 확장자를 통해 ContentType을 찾아온다.")
    void findResponseContentType_pathExtension() throws IOException {
        // given
        final HttpRequest httpRequest = createHttpRequest("/chart-pie.js", "*/*");

        // when
        final ContentType contentType = ContentType.findResponseContentTypeFromRequest(httpRequest);

        // then
        assertThat(contentType).isEqualTo(ContentType.JS);
    }

    @Test
    @DisplayName("Accept Type이 */* 이고, 정적 파일 요청이 아니라면 예외가 발생한다.")
    void findResponseContentType_notFileRequest() throws IOException {
        // given
        final HttpRequest httpRequest = createHttpRequest("/", "*/*");

        // when, then
        assertThatThrownBy(() -> ContentType.findResponseContentTypeFromRequest(httpRequest))
            .isInstanceOf(UnsupportedContentTypeException.class);
    }

    @Test
    @DisplayName("일치하는 ContentType이 없다면 예외가 발생한다.")
    void findResponseContentType_nonMatchAcceptType() throws IOException {
        // given
        final HttpRequest httpRequest = createHttpRequest("/favicon.ico", "image/jpg");

        // when, then
        assertThatThrownBy(() -> ContentType.findResponseContentTypeFromRequest(httpRequest))
            .isInstanceOf(UnsupportedContentTypeException.class);
    }
    
    private HttpRequest createHttpRequest(final String path, final String acceptType) throws IOException {
        String emptyLine = "\r\n";
        String startLine = "GET " + path + " HTTP/1.1 ";
        String headers = "Host: localhost:8080 " + emptyLine + "Connection: keep-alive ";

        if (acceptType != null) {
            headers = String.join(emptyLine, headers, "Accept: " + acceptType + " ");
        }

        final String httpRequestMessage = String.join(emptyLine, startLine, headers, emptyLine);

        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            return HttpRequest.from(reader);
        }
    }
}
