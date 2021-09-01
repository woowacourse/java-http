package nextstep.jwp.http.request;

import nextstep.jwp.http.exception.HttpFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestLineTest {
    RequestLine requestLine;

    @BeforeEach
    private void setUp() {
        requestLine = RequestLine.of("GET /register?test1=test1&test2=test2 HTTP/1.1");
    }

    @Test
    void checkMethod() {
        assertThat(requestLine.checkMethod("GET")).isTrue();
    }

    @Test
    void getPath() {
        assertThat(requestLine.getPath()).isEqualTo("/register");
    }

    @Test
    void getQueryParam() {
        assertThat(requestLine.getQueryParam("test1")).isEqualTo("test1");
        assertThat(requestLine.getQueryParam("test2")).isEqualTo("test2");
    }

    @Test
    void httpFormatException() {
        assertThatThrownBy(() -> {
            requestLine = RequestLine.of("GET /register?test1=test1&test2=test2 HTTP/1.1 ERROR");
        }).isInstanceOf(HttpFormatException.class);
    }
}
