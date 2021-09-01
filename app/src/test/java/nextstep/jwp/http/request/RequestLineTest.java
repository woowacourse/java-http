package nextstep.jwp.http.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
