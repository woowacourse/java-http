package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @DisplayName("HTTP 헤더 빌더 테스트")
    @Test
    void HttpHeadersBuilderTest() {

        Headers headers = new Headers.Builder()
            .header("Content-Type", "text/html;charset=utf-8")
            .header("Content-Length", "13")
            .build();

        assertThat(headers.getValue("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(headers.getValue("Content-Length")).isEqualTo("13");
    }

    @DisplayName("HTTP 헤더 생성 테스트")
    @Test
    void of() {
        Headers headers = Headers.of(Arrays.asList(
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 13"
        ));

        assertThat(headers.getValue("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(headers.getValue("Content-Length")).isEqualTo("13");
    }

    @DisplayName("잘못된 헤더 형식일 경우 예외 처리")
    @Test
    void invalidOf() {
        assertThatIllegalArgumentException().isThrownBy(() ->
            Headers.of(Arrays.asList(
                "Content-Type: text/html: charset=utf-8",
                "Content-Length: 13"
            ))
        );
    }

    @DisplayName("잘못된 헤더 형식일 경우 예외 처리2")
    @Test
    void invalidOf2() {
        assertThatIllegalArgumentException().isThrownBy(() ->
            Headers.of(Arrays.asList(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: "
            ))
        );
    }
}