package org.apache.coyote.http11.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.common.HttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpResponseTest {

    @Test
    void StatusCode와_HttpHeader를_넣으면_HttpReponse를_만들_수_있다() {
        // given
        StatusCode code = StatusCode.FOUND;
        HttpHeaders headers = HttpHeaders.create(Map.of("Location", "/index.html"));

        // when&then
        HttpResponse response = HttpResponse.create(code, headers);
    }

    @Test
    void Response를_byte로_변환할_수_있다() {
        // given
        StatusCode code = StatusCode.FOUND;
        HttpHeaders headers = HttpHeaders.create(Map.of("Location", "/index.html"));
        HttpResponse response = HttpResponse.create(code, headers);

        // when
        byte[] actual = response.getBytes();

        // then
        byte[] expected = String.join(System.lineSeparator(), "HTTP/1.1 302 Found ", "Location: /index.html ", "", "")
                .getBytes(StandardCharsets.UTF_8);

        assertThat(actual).isEqualTo(expected);
    }
}
