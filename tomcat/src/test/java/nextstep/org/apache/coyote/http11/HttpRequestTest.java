package nextstep.org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpMethodType;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestTest {

    @DisplayName("Request URI를 읽고 올바른 Request 객체를 생성한다.")
    @Test
    void from() {
        // given
        final String line = "GET /index.html HTTP/1.1 ";
        final HttpMethodType expectedMethodType = HttpMethodType.GET;
        final HttpPath expectedHttpPath = HttpPath.from("/index.html");

        // when
        final HttpRequest actual = HttpRequest.from(line);

        // then
        assertAll(
                () -> assertThat(actual.getHttpMethodType()).isEqualTo(expectedMethodType),
                () -> assertThat(actual.getHttpPath().getResource()).isEqualTo(expectedHttpPath.getResource()),
                () -> assertThat(actual.getHttpPath().getContentType()).isEqualTo(expectedHttpPath.getContentType()),
                () -> assertThat(actual.getHttpPath().getQueryParameter()).isEqualTo(expectedHttpPath.getQueryParameter())
        );
    }

    @DisplayName("Request URI가 존재하지 않으면 에외 처리한다.")
    @Test
    void from_nullRequest() {
        assertThatThrownBy(() -> HttpRequest.from(null))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("request가 존재하지 않습니다.");
    }
}
