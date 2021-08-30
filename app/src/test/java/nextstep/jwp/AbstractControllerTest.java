package nextstep.jwp;

import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @DisplayName("GET / HTTP/1.1 요청을 실행하여 메인 페이지 응답을 확인한다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(socket);

        // when
        dispatcherServlet.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        String result = socket.output();
        assertThat(result).contains(expected);
    }

    @DisplayName("GET /index.html HTTP/1.1 요청을 실행하여 인데스 페이지 응답을 확인한다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                GET + " /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(socket);

        // when
        dispatcherServlet.run();

        // then
        String body = FileUtils.readFileOfUrl("/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
//                "Content-Length: 5564 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                body;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /pomo HTTP/1.1 요청을 실행하여 잘못된 페이지 응답을 확인한다.")
    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                GET + " /pomo HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(socket);

        // when
        dispatcherServlet.run();

        // then
        String body = FileUtils.readFileOfUrl("/404.html");
        String response = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 2426 ",
                "Content-Length: 2477 ",
                "",
                body);
        assertThat(socket.output()).isEqualTo(response);
    }
}
