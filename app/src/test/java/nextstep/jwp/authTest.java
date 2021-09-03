package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import nextstep.jwp.http.RequestHandler;
import nextstep.jwp.http.auth.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class authTest {

    @DisplayName("로그인시 세션에 해당 세션 ID가 저장된다.")
    @Test
    void sessionIndex() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        int beforeLoginSessionSize = HttpSessions.getSessionSize();

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertThat(HttpSessions.getSessionSize()).isEqualTo(beforeLoginSessionSize + 1);
    }
}
