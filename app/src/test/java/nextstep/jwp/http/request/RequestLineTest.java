package nextstep.jwp.http.request;

import nextstep.jwp.http.exception.HttpFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {
    RequestLine requestLine;

    @BeforeEach
    private void setUp() {
        requestLine = RequestLine.of("GET /register?test1=test1&test2=test2 HTTP/1.1");
    }

    @DisplayName("HTTP 메소드 값을 비교한다.")
    @Test
    void checkMethod() {
        assertThat(requestLine.checkMethod("GET")).isTrue();
    }

    @DisplayName("요청 경로를 얻어온다.")
    @Test
    void getPath() {
        assertThat(requestLine.getPath()).isEqualTo("/register");
    }

    @DisplayName("URL의 쿼리 파라미터 값을 얻어온다.")
    @Test
    void getQueryParam() {
        assertThat(requestLine.getQueryParam("test1")).isEqualTo("test1");
        assertThat(requestLine.getQueryParam("test2")).isEqualTo("test2");
    }

    @DisplayName("Request Line의 형식이 잘못 전달되는 예외")
    @Test
    void httpFormatException() {
        assertThatThrownBy(() -> {
            requestLine = RequestLine.of("GET /register?test1=test1&test2=test2 HTTP/1.1 ERROR");
        }).isInstanceOf(HttpFormatException.class);
    }
}
