package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.ContentTooLargeException;
import org.apache.coyote.http11.exception.NotImplementedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @Test
    void 없으면_0이다() {
        assertThat(RequestHeaders.from(List.of()).getContentLength()).isZero();
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "5, 5", "005, 5", "2097152, 2097152"})   // 마지막은 2MB 정확히
    void 유효한_값을_파싱한다(final String raw, final int expected) {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Length: " + raw));

        assertThat(headers.getContentLength()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",          // 빈 값
            "+5",        // 부호
            "-1",        // 음수
            "5.0",       // 소수점
            "5, 5",      // 여러 값
            "0x10",      // 16진수
            "1e3",       // 지수 표기
            "٥",         // 아랍 숫자 5
            "５",        // 전각 5
    })
    void 형식이_잘못되면_400(final String raw) {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("Content-Length: " + raw)))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2097153", "2147483648", "99999999999999999999"})
    void 한도를_넘으면_413(final String raw) {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("Content-Length: " + raw)))
                .isInstanceOf(ContentTooLargeException.class);
    }

    @Test
    void 대소문자가_달라도_중복을_거부한다() {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("Content-Length: 5", "content-length: 5")))
                .isInstanceOf(BadRequestException.class);
    }
    @Nested
    class Transfer_Encoding {

        @ParameterizedTest
        @ValueSource(strings = {"chunked", "Chunked", "gzip, chunked", "identity", "xchunked", ""})
        void 값과_관계없이_있으면_501(final String value) {
            assertThatThrownBy(() -> RequestHeaders.from(List.of("Transfer-Encoding: " + value)))
                    .isInstanceOf(NotImplementedException.class);
        }

        @Test
        void 이름의_대소문자가_달라도_인식한다() {
            assertThatThrownBy(() -> RequestHeaders.from(List.of("transfer-ENCODING: chunked")))
                    .isInstanceOf(NotImplementedException.class);
        }

        @Test
        void Content_Length와_함께_오면_400() {
            final List<String> lines = List.of("Content-Length: 4", "Transfer-Encoding: chunked");

            assertThatThrownBy(() -> RequestHeaders.from(lines))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Transfer-Encoding");
        }

        @Test
        void 순서가_바뀌어도_400() {
            final List<String> lines = List.of("Transfer-Encoding: chunked", "Content-Length: 4");

            assertThatThrownBy(() -> RequestHeaders.from(lines))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Transfer-Encoding");
        }

        @Test
        void Content_Length가_잘못돼도_smuggling으로_먼저_판단한다() {
            final List<String> lines = List.of("Content-Length: abc", "Transfer-Encoding: chunked");

            assertThatThrownBy(() -> RequestHeaders.from(lines))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Transfer-Encoding");
        }

        @Test
        void 없으면_통과한다() {
            assertThatCode(() -> RequestHeaders.from(List.of("Content-Length: 5")))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class Host {

        @ParameterizedTest
        @ValueSource(strings = {"localhost", "localhost:8080", "127.0.0.1:8080", "[::1]:8080", "example.com"})
        void 유효한_Host는_허용한다(final String value) {
            final RequestHeaders headers = RequestHeaders.from(List.of("Host: " + value));

            assertThat(headers.get(HttpHeaderName.HOST)).hasValue(value);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",                  // 빈 값
                "local host",        // 공백
                "localhost/evil",    // 경로 구분자
                "user@localhost",    // 사용자 정보
                "localhost\\evil",   // 역슬래시
                "localhost?x",       // 쿼리 구분자
                "localhost#x",       // 프래그먼트 구분자
                "호스트",             // 비ASCII
        })
        void 형식이_잘못된_Host는_거부한다(final String value) {
            assertThatThrownBy(() -> RequestHeaders.from(List.of("Host: " + value)))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void Host가_두_개면_거부한다() {
            final List<String> lines = List.of("Host: localhost", "host: evil.com");

            assertThatThrownBy(() -> RequestHeaders.from(lines))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("host");
        }

        @Test
        void Host가_없어도_헤더_파싱은_성공한다() {
            final RequestHeaders headers = RequestHeaders.from(List.of("Content-Type: text/html"));

            assertThat(headers.hasHost()).isFalse();
        }
    }

    @Nested
    class 중복_헤더 {

        @ParameterizedTest
        @ValueSource(strings = {"Host", "Content-Length", "Content-Type"})
        void 단일_값_헤더가_중복되면_거부한다(final String name) {
            final String value = name.equals("Content-Length") ? "5" : "a";
            final List<String> lines = List.of(name + ": " + value, name.toLowerCase() + ": " + value);

            assertThatThrownBy(() -> RequestHeaders.from(lines))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("중복");
        }

        @Test
        void Cookie는_세미콜론으로_합친다() {
            final List<String> lines = List.of("Cookie: a=1", "Cookie: b=2");

            final RequestHeaders headers = RequestHeaders.from(lines);

            assertThat(headers.get(HttpHeaderName.COOKIE)).hasValue("a=1; b=2");
        }

        @Test
        void 합쳐진_Cookie를_모두_파싱한다() {
            final List<String> lines = List.of("Cookie: a=1", "Cookie: b=2");

            final HttpCookie cookie = RequestHeaders.from(lines).getCookie();

            assertThat(cookie.get("a")).hasValue("1");
            assertThat(cookie.get("b")).hasValue("2");
        }

        @Test
        void 빈_Cookie_줄은_구분자를_남기지_않는다() {
            final List<String> lines = List.of("Cookie: a=1", "Cookie:");

            assertThat(RequestHeaders.from(lines).get(HttpHeaderName.COOKIE)).hasValue("a=1");
        }
    }
}