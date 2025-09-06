package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void 헤더_라인들로부터_HttpHeaders를_생성한다() {
        // given
        List<String> headerLines = List.of(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 23",
                "Host: localhost:8080",
                "Accept: */*"
        );

        // when
        HttpHeaders httpHeaders = HttpHeaders.fromLines(headerLines);

        // then
        assertAll(
                () -> assertThat(httpHeaders.get("Content-Type")).containsExactly("text/html;charset=utf-8"),
                () -> assertThat(httpHeaders.get("Content-Length")).containsExactly("23"),
                () -> assertThat(httpHeaders.get("Host")).containsExactly("localhost:8080"),
                () -> assertThat(httpHeaders.get("Accept")).containsExactly("*/*")
        );
    }

    @Test
    void 존재하지_않는_헤더를_조회하면_빈_리스트를_반환한다() {
        // given
        HttpHeaders httpHeaders = HttpHeaders.fromLines(List.of("Host: localhost:8080"));

        // when
        List<String> values = httpHeaders.get("Non-Existent-Header");

        // then
        assertThat(values).isEmpty();
    }

    @Test
    void 빈_줄이나_잘못된_형식의_헤더는_무시한다() {
        // given
        List<String> headerLines = List.of(
                "Content-Type: text/html;charset=utf-8",
                "",
                "Content-Length: 23",
                "Invalid-Header",
                "Host: localhost:8080"
        );

        // when
        HttpHeaders httpHeaders = HttpHeaders.fromLines(headerLines);

        // then
        assertAll(
                () -> assertThat(httpHeaders.get("Content-Type")).containsExactly("text/html;charset=utf-8"),
                () -> assertThat(httpHeaders.get("Content-Length")).containsExactly("23"),
                () -> assertThat(httpHeaders.get("Host")).containsExactly("localhost:8080"),
                () -> assertThat(httpHeaders.get("Invalid-Header")).isEmpty()
        );
    }

    @Test
    void 동일한_이름의_헤더가_여러_개_있을_경우_모두_저장한다() {
        // given
        List<String> headerLines = List.of(
                "Accept: text/html",
                "Accept: application/xhtml+xml",
                "Accept: application/xml;q=0.9"
        );

        // when
        HttpHeaders httpHeaders = HttpHeaders.fromLines(headerLines);

        // then
        assertThat(httpHeaders.get("Accept")).containsExactly(
                "text/html",
                "application/xhtml+xml",
                "application/xml;q=0.9"
        );
    }

    @Test
    void 헤더_키의_대소문자를_구분하지_않는다() {
        // given
        List<String> headerLines = List.of(
                "host: localhost:8080",
                "Host: localhost:8081"
        );

        // when
        HttpHeaders httpHeaders = HttpHeaders.fromLines(headerLines);

        // then
        assertThat(httpHeaders.get("host")).containsExactly("localhost:8080", "localhost:8081");
    }
}
