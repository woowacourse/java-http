package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class AuthControllerTest {

    @DisplayName("로그인이 성공하면 302를 반환하고 /index.html 리다이렉트한다.")
    @Test
    void authUserLogin() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");
        var expected = "HTTP/1.1 302 Moved Permanently \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 유저일 401 페이지가 보여진다.")
    @Test
    void notExistUserException() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/401.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("잘못된 param으로 UserRequest를 생성하여 예외가 발생한다.")
    @Test
    void queryParamNotFoundException() {
        final AuthController authController = new AuthController();
        final String startLine = "GET /login?account1=gu&password=password HTTP/1.1";

        assertThatThrownBy(() ->
                authController.run(startLine))
                .hasMessageContaining("잘못된 queryParam 입니다.")
                .isInstanceOf(QueryParamNotFoundException.class);
    }
}
