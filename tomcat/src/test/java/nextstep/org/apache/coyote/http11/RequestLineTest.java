package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequestLineTest {

    @DisplayName("쿼리 스트링이 없는 경우, Http 요청 라인이 잘 바인딩 되는지 확인한다.")
    @Test
    void of_notExist_queryString() {
        // given
        final String value = "GET / HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.of(value);

        // then
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getUrl()).isEqualTo("/"),
                () -> assertThat(requestLine.getQueryParams().size()).isEqualTo(0)
        );
    }

    @DisplayName("쿼리 스트링이 있는 경우, Http 요청 라인이 잘 바인딩 되는지 확인한다.")
    @Test
    void of_exist_queryString() {
        // given
        final String value = "GET /login?account=gugu&password=password HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.of(value);

        // then
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getUrl()).isEqualTo("/login"),
                () -> assertThat(requestLine.getQueryParams().size()).isEqualTo(2)
        );
    }
}
