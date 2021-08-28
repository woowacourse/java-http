package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("HTTP 헤더 빌더 테스트")
    @Test
    void HttpHeadersBuilderTest() {

        HttpHeaders headers = new HttpHeaders.Builder()
            .header("Content-Type", "text/html;charset=utf-8")
            .header("Content-Length", "13")
            .build();

        assertThat(headers.getValue("Content-Type")).isEqualTo(Collections.singletonList("text/html;charset=utf-8"));
        assertThat(headers.getValue("Content-Length")).isEqualTo(Collections.singletonList("13"));
    }

    @DisplayName("HTTP 헤더 생성 테스트")
    @Test
    void of() {
        HttpHeaders headers = HttpHeaders.of(Arrays.asList(
           "Content-Type: text/html;charset=utf-8",
           "Content-Length: 13"
        ));

        assertThat(headers.getValue("Content-Type")).isEqualTo(Collections.singletonList("text/html;charset=utf-8"));
        assertThat(headers.getValue("Content-Length")).isEqualTo(Collections.singletonList("13"));
    }

    @DisplayName("잘못된 헤더 형식일 경우 예외 처리")
    @Test
    void invalidOf() {
        assertThatIllegalArgumentException().isThrownBy(() ->
            HttpHeaders.of(Arrays.asList(
                "Content-Type: text/html: charset=utf-8",
                "Content-Length: 13"
            ))
        );
    }

    @DisplayName("잘못된 헤더 형식일 경우 예외 처리2")
    @Test
    void invalidOf2() {
        assertThatIllegalArgumentException().isThrownBy(() ->
            HttpHeaders.of(Arrays.asList(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: "
            ))
        );
    }
}