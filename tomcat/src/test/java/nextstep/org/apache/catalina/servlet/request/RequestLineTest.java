package nextstep.org.apache.catalina.servlet.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.catalina.servlet.request.InvalidStartLineException;
import org.apache.catalina.servlet.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("RequestLine 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void 시작_줄이_올바르지_않은_형식이라면_예외() {
        // given
        String invalidStartLine = "GET HTTP/1.1";

        // when & then
        assertThatThrownBy(() ->
                RequestLine.from(invalidStartLine)
        ).isInstanceOf(InvalidStartLineException.class);
    }

    @Test
    void null인_경우_빈_객체가_반환된다() {
        // given
        RequestLine requestLine = RequestLine.from(null);

        // when & then
        assertThat(requestLine.isEmpty()).isTrue();
    }

    @Test
    void 올바른_경우_잘_생성된다() {
        // when
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        // then
        assertThat(requestLine.method()).isEqualTo("GET");
        assertThat(requestLine.uri().path()).isEqualTo("/index.html");
        assertThat(requestLine.httpVersion()).isEqualTo("HTTP/1.1");
    }
}
