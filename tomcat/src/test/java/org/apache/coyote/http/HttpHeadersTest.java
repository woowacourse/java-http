package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeadersTest {

    @Test
    void 헤더를_생성한다() {
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Content-Type: application/json",
                "Content-Length: 100"
        ));

        assertThat(headers.get("Content-Type")).contains("application/json");
        assertThat(headers.get("Content-Length")).contains("100");
        assertThat(headers.contentType()).isEqualTo("application/json");
        assertThat(headers.contentLength()).isEqualTo(100);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Content-Length: abc",  //문자인 경우
            "Content-Length: -1"    //음수인 경우
    })
    void 잘못된_Content_Length_인_경우_예외가_발생한다(String line) {
        assertThatThrownBy(() -> HttpHeaders.from(List.of(line)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
