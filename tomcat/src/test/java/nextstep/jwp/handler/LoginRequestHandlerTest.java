package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LoginRequestHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LoginRequestHandlerTest {

    private final LoginRequestHandler handler = new LoginRequestHandler();

    @Test
    void 로그인_요청을_처리한다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET /login?account=gugu&password=password HTTP/1.1"))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3796 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toString()).isEqualTo(expected);
    }
}
