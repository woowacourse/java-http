package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidRequestLineException;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void RequestLine을_반환할_수_있다() {
        // given
        String uri = "GET /login?account=rookie&password=password HTTP/1.1 ";

        // when
        RequestLine requestLine = RequestLine.create(uri);

        // then
        assertThat(requestLine).isInstanceOf(RequestLine.class);
    }

    @Test
    void RequestLine의_패턴에_어긋나는_경우_예외가_발생한다() {
        // given
        String uri = "GET /login?account=rookie&password=password";

        // when & then
        assertThatThrownBy(() -> RequestLine.create(uri))
            .isInstanceOf(InvalidRequestLineException.class);
    }

    @Test
    void uri의_확장자를_반환할_수_있다() {
        // given
        String uri = "GET /index.html HTTP/1.1 ";

        // when
        RequestLine requestLine = RequestLine.create(uri);

        // then
        assertThat(requestLine.getRequestExtension()).isEqualTo("html");
    }
}
