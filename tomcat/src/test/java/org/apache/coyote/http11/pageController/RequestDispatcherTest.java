package org.apache.coyote.http11.pageController;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.BadRequestException;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestDispatcherTest {
    private final SessionManager sessionManager = new SessionManager();
    private final RequestDispatcher requestDispatcher = new RequestDispatcher();

    @Test
    void mappedController() {
        assertThat(requestDispatcher.getPageController("/login")).isInstanceOf(LoginController.class);
    }

    @Test
    void sameInstanceForEveryRequest() {
        assertThat(requestDispatcher.getPageController("/login"))
                .isSameAs(requestDispatcher.getPageController("/login"));
    }

    @Test
    void staticResourceControllerByDefault() {
        assertThat(requestDispatcher.getPageController("/index.html"))
                .isInstanceOf(StaticResourceController.class);
    }

    @Test
    void internalServerErrorWhenControllerThrowsIOException() throws IOException {
        // given
        final RequestDispatcher dispatcher = dispatcherThrowing(new IOException("파일을 읽을 수 없습니다."));

        // when
        final HttpResponse response = dispatcher.dispatch(indexRequest());

        // then
        assertThat(toString(response)).isEqualTo(serverErrorResponse());
    }

    @Test
    void internalServerErrorWhenControllerThrowsRuntimeException() throws IOException {
        // given
        final RequestDispatcher dispatcher = dispatcherThrowing(new IllegalStateException("예상하지 못한 오류"));

        // when
        final HttpResponse response = dispatcher.dispatch(indexRequest());

        // then
        assertThat(toString(response)).isEqualTo(serverErrorResponse());
    }

    @Test
    void badRequestExceptionIsNotHandledHere() {
        // given
        final BadRequestException exception = new BadRequestException("잘못된 정적 리소스 경로입니다: /../secret");
        final RequestDispatcher dispatcher = dispatcherThrowing(exception);

        // when & then
        assertThatThrownBy(() -> dispatcher.dispatch(indexRequest()))
                .isSameAs(exception);
    }

    private RequestDispatcher dispatcherThrowing(Exception exception) {
        return new RequestDispatcher(Map.of(), request -> {
            if (exception instanceof IOException ioException) {
                throw ioException;
            }
            throw (RuntimeException) exception;
        });
    }

    private HttpRequest indexRequest() {
        return HttpRequest.from("GET /index.html HTTP/1.1", HttpHeaders.empty(), HttpBody.empty(), sessionManager);
    }

    private String toString(HttpResponse response) {
        return new String(response.toBytes(), StandardCharsets.UTF_8);
    }

    private String serverErrorResponse() throws IOException {
        final String body = readResource("static/500.html");

        return String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);
    }

    private String readResource(String resourceName) throws IOException {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName);

        try (InputStream inputStream = Objects.requireNonNull(resourceStream)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
