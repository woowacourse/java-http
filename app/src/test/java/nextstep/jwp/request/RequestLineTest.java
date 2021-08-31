package nextstep.jwp.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("RequestLine을 파싱한다.")
    void parseRequestLine() {
        // given
        final RequestLine requestLine = new RequestLine("GET /index HTTP/1.1 ");

        HttpMethod httpMethod = requestLine.getHttpMethod();
        String uri = requestLine.getUri();

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
        assertThat(uri).isEqualTo("/index");
    }

    @Test
    @DisplayName("길이가 올바르지 않은 requestLine인 경우 예외가 발생한다.")
    void invalidRequestLine() {
        assertThatThrownBy(() -> new RequestLine("Get / "))
                .isInstanceOf(HttpException.class);
    }
}