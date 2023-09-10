package org.apache.coyote.http11;

import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.DuplicationMemberException;
import org.apache.coyote.request.MockRequestReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.coyote.http11.ViewFileFixture.PAGE_REGISTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
public class RegisterApiTest {

    @Test
    void 회원가입_페이지_정적_요청_확인() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        assertThat(socket.output())
                .contains(PAGE_REGISTER, "HTTP/1.1 200 OK");
    }

    @Test
    void 회원가입_페이지_viewName_요청_확인() {
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        assertThat(socket.output())
                .contains(PAGE_REGISTER, "HTTP/1.1 200 OK");
    }

    @Test
    void post_요청시_회원가입_성공() throws IOException {
        final List<String> requestLines = new ArrayList<>(Arrays.asList(
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 40 ",
                ""));

        final String requestBody = "account=kero&password=keroro&email=kero@kero.com";
        final Request request = Request.from(new MockRequestReader(requestLines, requestBody));
        final RegisterController registerController = new RegisterController();
        final ResponseEntity responseEntity = registerController.handle(request);

        assertThat(responseEntity.toString()).contains(PAGE_REGISTER, "HTTP/1.1 302 Temporary Redirect", "Location: /index.html");
    }

    @Test
    void post_요청시_이미_가입된_회원_아이디로_가입하면_예외_발생() throws IOException {
        final List<String> requestLines = new ArrayList<>(Arrays.asList(
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 40 ",
                ""));

        final String requestBody = "account=gugu&password=password&email=gugu@gugu.com";
        final Request request = Request.from(new MockRequestReader(requestLines, requestBody));
        final RegisterController registerController = new RegisterController();

        assertThatThrownBy(() -> registerController.handle(request))
                .isExactlyInstanceOf(DuplicationMemberException.class);
    }
}
