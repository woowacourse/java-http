package org.apache.coyote.publisher;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.common.MessageBody;
import org.apache.coyote.request.RequestBody;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InputStreamRequestPublisherTest {

    @ParameterizedTest
    @CsvSource(value = {
            "GET_/index.html_''_HTTP/1.1",
            "POST_/index.html_''_HTTP/1.1",
            "GET_/login_?name=hyena_HTTP/1.1",
            "GET_/login_?name=hyena_HTTP/1.1"
    }, delimiter = '_')
    void InputStreamReader를_받아_HttpRequest로_파싱에_성공한다(final String httpMethod, final String path, final String queryParams, final String httpVersion) {
        // given
        final String requestLine = httpMethod + " " + path + queryParams + " " + httpVersion + " \r\n\r\n";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(requestLine.getBytes(UTF_8));

        // when
        final HttpRequest actual = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // then
        final String actualHttpMethod = actual.requestLine().httpMethod().name();
        final String actualRequestPath = actual.requestLine().requestPath().value();
        final String actualHttpVersion = actual.requestLine().httpVersion().version();
        final RequestBody actualRequestBody = actual.requestBody();

        assertAll(
                () -> assertThat(actualHttpMethod).isEqualTo(httpMethod),
                () -> assertThat(actualRequestPath).isEqualTo(path),
                () -> assertThat(actualHttpVersion).isEqualTo(httpVersion),
                () -> assertThat(actualRequestBody).isEqualTo(RequestBody.from(MessageBody.empty()))
        );
    }
}
