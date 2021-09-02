package nextstep.jwp;

import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    public static final String CONTENT_LENGTH = "Content-Length: ";

    @DisplayName("GET / HTTP/1.1 요청을 실행하여 메인 페이지 응답을 확인한다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(outputs[1]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[2]).isEqualTo(CONTENT_LENGTH + "12 ");
        assertThat(outputs[3]).isBlank();
        assertThat(outputs[4]).isEqualTo("Hello world!");
    }

    @DisplayName("GET /index.html HTTP/1.1 요청을 실행하여 인덱스 페이지 응답을 확인한다.")
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
        final HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(outputs[1]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[2]).contains(CONTENT_LENGTH);
        assertThat(outputs[2].substring(CONTENT_LENGTH.length())).isNotBlank();
        assertThat(outputs[3]).isBlank();
        int bodyStartIndex = output.indexOf(outputs[4]);
        assertThat(output.substring(bodyStartIndex)).isEqualTo(FileUtils.readFileOfUrl("/index.html"));
    }

    @DisplayName("GET /pomo HTTP/1.1 요청을 실행하여 잘못된 페이지 응답을 확인한다.")
    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                GET + " /pomo HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String output = socket.output();
        String[] outputs = output.split("\r\n");
        assertThat(outputs[0]).isEqualTo("HTTP/1.1 404 Not Found ");
        assertThat(outputs[1]).isEqualTo("Content-Type: text/html;charset=utf-8 ");
        assertThat(outputs[2]).contains(CONTENT_LENGTH);
        assertThat(outputs[2].substring(CONTENT_LENGTH.length())).isNotBlank();
        assertThat(outputs[3]).isBlank();
        int bodyStartIndex = output.indexOf(outputs[4]);
        assertThat(output.substring(bodyStartIndex)).isEqualTo(FileUtils.readFileOfUrl("/404.html"));
    }
}
