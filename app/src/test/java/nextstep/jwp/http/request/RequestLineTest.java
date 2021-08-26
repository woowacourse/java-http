package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestLine 테스트")
class RequestLineTest {

    @DisplayName("RequestLine 파싱 테스트")
    @Test
    void parseRequestLine() {
        //given
        final String requestLineValue = "GET /login?account=inbi&password=1234 HTTP/1.1";

        //when
        final RequestLine requestLine = new RequestLine(requestLineValue);

        //then
        assertThat(requestLine.hasMethod(HttpMethod.GET)).isTrue();
        assertThat(requestLine.getUri()).isEqualTo("/login");
    }
}