package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@DisplayName("ResponseHeaders 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ResponseHeadersTest {

    @Test
    void 설정된_헤더들로_응답_메세지를_만든다() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.put("Mallang", "말랑");
        responseHeaders.put("Ohjintaek", "썬샷");

        // when
        String result = responseHeaders.toString();

        // then
        assertThat(result).isEqualTo(
                "Mallang: 말랑 \r\n" +
                        "Ohjintaek: 썬샷 \r\n" +
                        "\r\n"
        );
    }
}
