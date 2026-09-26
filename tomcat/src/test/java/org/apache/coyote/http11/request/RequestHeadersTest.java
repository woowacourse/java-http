package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHeadersTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "Host : localhost",        // 콜론 앞 공백
            "Host\t: localhost",       // 콜론 앞 탭
            ": localhost",             // 빈 이름
            "Con tent-Length: 5",      // 이름 중간 공백
            "Host(x): localhost",      // 구분자 문자
            "헤더: value",              // 비ASCII
            "Coo\u212Aie: a=1",        // 켈빈 기호 K
    })
    void 잘못된_헤더_이름은_거부한다(final String line) {
        assertThatThrownBy(() -> RequestHeaders.from(List.of(line)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 값에는_콜론이_포함될_수_있다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Host: localhost:8080"));

        assertThat(headers.get(HttpHeaderName.HOST)).hasValue("localhost:8080");
    }

    @Test
    void 콜론_뒤_공백은_없어도_된다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Host:localhost"));

        assertThat(headers.get(HttpHeaderName.HOST)).hasValue("localhost");
    }

    @Test
    void token_특수문자가_포함된_이름은_허용한다() {
        assertThatCode(() -> RequestHeaders.from(List.of("X-Custom_Header.v1: a")))
                .doesNotThrowAnyException();
    }
    @Test
    void 헤더_이름은_대소문자를_구분하지_않는다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("content-length: 5"));

        assertThat(headers.getContentLength()).isEqualTo(5);
    }

    @Test
    void 대소문자가_달라도_Content_Length_중복을_거부한다() {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("Content-Length: 5", "content-length: 5")))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Host: localhost", "\tHost: localhost", "   ", "\t", " "})
    void 공백이나_탭으로_시작하는_줄은_obs_fold로_거부한다(final String line) {
        assertThatThrownBy(() -> RequestHeaders.from(List.of(line)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("obs-fold");
    }

    @Test
    void 이전_헤더의_이어_쓰기도_거부한다() {
        final List<String> lines = List.of("X-Foo: bar", " Content-Length: 5");

        assertThatThrownBy(() -> RequestHeaders.from(lines))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("obs-fold");
    }
    @Test
    void 값의_앞뒤_SP와_HTAB만_제거한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Type: \t text/html \t "));

        assertThat(headers.get(HttpHeaderName.CONTENT_TYPE)).hasValue("text/html");
    }

    @Test
    void 값_중간의_공백은_유지한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Type: text/html;  charset=utf-8"));

        assertThat(headers.get(HttpHeaderName.CONTENT_TYPE)).hasValue("text/html;  charset=utf-8");
    }

    @Test
    void 유니코드_공백은_지우지_않고_값으로_유지한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Type: \u3000text/html"));

        assertThat(headers.get(HttpHeaderName.CONTENT_TYPE)).hasValue("\u3000text/html");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Content-Type: a\rContent-Length: 100",   // 값 중간의 CR
            "Content-Type: a\0b",                       // NUL
            "Content-Type: a\u000Bb",                   // 수직 탭
            "Content-Type: a\u001Fb",                   // 유닛 구분자
            "Content-Type: a\u007Fb",                   // DEL
            "Content-Length: 5\u000B",                  // strip()이면 5로 통과하던 케이스
    })
    void 값에_제어_문자가_있으면_거부한다(final String line) {
        assertThatThrownBy(() -> RequestHeaders.from(List.of(line)))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Content-Type:", "Content-Type:   ", "Content-Type:\t"})
    void 빈_값은_허용한다(final String line) {
        final RequestHeaders headers = RequestHeaders.from(List.of(line));

        assertThat(headers.get(HttpHeaderName.CONTENT_TYPE)).hasValue("");
    }
}