package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Request Line")
class RequestLineTest {
    private static final String GET_LINE = "GET /index.html HTTP/1.1";
    private static final String POST_LINE = "POST /register HTTP/1.1";

    private static final RequestLine GET_REQUEST_LINE = new RequestLine(GET_LINE);
    private static final RequestLine POST_REQUEST_LINE = new RequestLine(POST_LINE);

    @DisplayName("Request Line 객체를 성공적으로 생성한다.")
    @Test
    void create() {
        assertDoesNotThrow(() -> {
            new RequestLine(GET_LINE);
            new RequestLine(POST_LINE);
        });
    }

    @DisplayName("HTTP 메서드를 확인한다.")
    @Test
    void method() {
        assertThat(GET_REQUEST_LINE.isGet()).isTrue();
        assertThat(POST_REQUEST_LINE.isPost()).isTrue();
    }

    @DisplayName("path를 확인한다.")
    @Test
    void path() {
        String getActual = GET_REQUEST_LINE.getPath();
        String postActual = POST_REQUEST_LINE.getPath();

        assertThat(getActual).isEqualTo("/index.html");
        assertThat(postActual).isEqualTo("/register");
    }

    @DisplayName("path와 queryparam이 분리되었는지 확인한다.")
    @Test
    void pathAndQuery() {
        String line = "GET /login?account=gugu&password=password HTTP/1.1";
        RequestLine requestLine = new RequestLine(line);

        String path = requestLine.getPath();
        String account = requestLine.getQueryParams("account");
        String password = requestLine.getQueryParams("password");

        assertAll(
                () -> assertThat(path).isEqualTo("/login"),
                () -> assertThat(account).isEqualTo("gugu"),
                () -> assertThat(password).isEqualTo("password")
        );
    }
}