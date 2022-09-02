package nextstep.org.apache.coyote.http11;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpMessageSupporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMessageSupporterTest {

    @Test
    void 요청에서_URI_추출하기() throws IOException {
        // given
        final var actual = "/index.html";
        final var httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var bytes = httpRequest.getBytes(StandardCharsets.UTF_8);

        try (final var byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            // when
            final var expected = HttpMessageSupporter.getRequestURI(byteArrayInputStream);

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"/,hello world", "/index.html,첫 페이지"})
    void 요청URI에_해당하는_리소스를_읽어서_HTTP_MESSAGE를_만든다(final String requestURI, final String actualContent)
            throws IOException {
        // given, when
        final var expected = HttpMessageSupporter.getHttpMessage(requestURI);

        // then
        assertThat(expected).containsIgnoringCase(actualContent);
    }
}
