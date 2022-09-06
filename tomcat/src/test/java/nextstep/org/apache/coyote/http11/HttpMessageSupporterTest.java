package nextstep.org.apache.coyote.http11;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.util.HttpMessageSupporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMessageSupporterTest {

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/"})
    void 요청에서_URI_추출하기(final String requestURI) throws IOException {
        // given
        final var expected = "/index.html";
        final var httpRequest = "GET " + requestURI + " HTTP/1.1 ";

        // when
        final var actual = HttpMessageSupporter.getRequestURI(httpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"/index.html,첫 페이지", "/invalid.html,This requested URL was not found on this server."})
    void 요청URI에_해당하는_리소스를_읽어서_HTTP_MESSAGE를_만든다(final String requestURI, final String expectedContent)
            throws IOException {
        // given, when
        final var actual = HttpMessageSupporter.getHttpMessageWithStaticResource(requestURI);

        // then
        assertThat(actual).containsIgnoringCase(expectedContent);
    }
}
