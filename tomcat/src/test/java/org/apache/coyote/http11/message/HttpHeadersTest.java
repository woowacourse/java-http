package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("메세지의 header 줄들로부터 HttpHeaders 를 생성한다.")
    void from() {
        // given
        final List<String> headerLines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        // when
        final HttpHeaders headers = HttpHeaders.from(headerLines);

        // then
        assertThat(headers.getHeadersWithValue())
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
            ));
    }

    @Test
    @DisplayName("헤더 필드에 여러 값이 있으면 그 중 첫 번째 값을 가져온다.")
    void findFirstValueOfField_exist() {
        // given
        final List<String> headerLines = List.of("Accept: text/html,*/* ");
        final HttpHeaders headers = HttpHeaders.from(headerLines);

        // when
        final Optional<String> firstValueOfField = headers.findFirstValueOfField("Accept");

        // then
        assertThat(firstValueOfField).isPresent()
            .get()
            .isEqualTo("text/html");
    }

    @Test
    @DisplayName("헤더의 첫 번째 값을 가져올 때, 해당 헤더 필드가 없으면 Optional.empty() 가 반환된다.")
    void findFirstValueOfField_notExist() {
        // given
        final List<String> headerLines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final HttpHeaders headers = HttpHeaders.from(headerLines);

        // when
        final Optional<String> firstValueOfField = headers.findFirstValueOfField("Accept");

        // then
        assertThat(firstValueOfField).isEmpty();
    }
}
