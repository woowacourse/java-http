package org.apache.coyote.http11.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.servlet.Page;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpResponseTest {

    @Test
    void Response를_byte로_변환할_수_있다() {
        // given
        HttpResponse response = HttpResponse.create();
        response.setStatusCode(StatusCode.FOUND);
        response.setLocation(Page.INDEX);

        // when
        byte[] actual = response.getBytes();

        // then
        byte[] expected = String.join(System.lineSeparator(), "HTTP/1.1 302 Found ", "Location: /index.html ", "", "")
                .getBytes(StandardCharsets.UTF_8);

        assertThat(actual).isEqualTo(expected);
    }
}
