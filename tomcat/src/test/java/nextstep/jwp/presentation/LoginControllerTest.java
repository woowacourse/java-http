package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class LoginControllerTest {

    private StubSocket httpGet(final String url) {
        final String httpRequest = String.join("\r\n",
            "GET " + url + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        return new StubSocket(httpRequest);
    }

    private StubSocket httpPost(final String url, final String responseBody) {
        final String httpRequest = String.join("\r\n",
            "POST " + url + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
        return new StubSocket(httpRequest);
    }

    @Test
    void GET_login_를_호출하면_로그인_페이지로_이동한다() throws IOException {
        // given
        final var socket = httpGet("/login");
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = socket.output();
        assertThat(httpResponse).contains("200 OK");
    }

    @Test
    void POST_login_호출과_로그인_정보를_입력하면_로그인이_완료되고_메인페이지로_이동한다() throws IOException {
        // given
        InMemoryUserRepository.save(new User(1L, "gugu", "password", "hkkang@woowahan.com"));

        final var socket = httpPost("/login", "account=gugu&password=password");
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = socket.output();

        assertAll(
            () -> assertThat(httpResponse).contains("200 OK"),
            () -> assertThat(httpResponse).contains("Set-Cookie")
        );
    }
}
