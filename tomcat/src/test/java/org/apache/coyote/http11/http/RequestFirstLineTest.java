package org.apache.coyote.http11.http;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class RequestFirstLineTest {

    @Test
    void requestFirstLine이_method_uri_version_으로_구성되지_않으면_예외발생() {
        String invalidRequestFirstLine = "GET HTTP/1.1 ";

        assertThatThrownBy(() -> RequestFirstLine.from(invalidRequestFirstLine))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void queryString을_파싱한다() {
        String input = "GET /login?name=leo&password=1234 HTTP/1.1 ";

        RequestFirstLine requestFirstLine = RequestFirstLine.from(input);

        Map<String, String> queryStrings = requestFirstLine.getQueryStrings();

        assertAll(
                () -> assertThat(queryStrings.size()).isEqualTo(2),
                () -> assertThat(queryStrings.get("name")).isEqualTo("leo"),
                () -> assertThat(queryStrings.get("password")).isEqualTo("1234")
        );
    }
}
