package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.apache.coyote.http11.exception.UnsupportedContentTypeException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ContentTypeTest {

    @ParameterizedTest
    @MethodSource("inputWithOutputData")
    @DisplayName("Accept Type에 따른 Content Type을 찾는다.")
    void findResponseContentTypeFromRequest(final String url, final String accept, final ContentType expected) throws IOException {
        // given
        final HttpRequest httpRequest = createHttpRequest(url, accept);

        // when
        final ContentType contentType = ContentType.findResponseContentTypeFromRequest(httpRequest);

        // then
        assertThat(contentType).isEqualTo(expected);
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

    private static Stream<Arguments> inputWithOutputData() {
        return Stream.of(
            Arguments.of("/styles.css", "text/css", ContentType.CSS),
            Arguments.of("/styles.css", null, ContentType.CSS),
            Arguments.of("/chart-pie.js", "*/*", ContentType.JS)
        );
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
