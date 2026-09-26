package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Session;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void POST_요청의_메서드와_경로_헤더_본문을_읽는다() throws IOException {
        String body = "account=고래&password=비밀";
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );
        var input = new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.readFrom(input);

        assertThat(request.findHeader("Content-Length"))
                .contains(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        assertThat(request.findFormParameter("account")).contains("고래");
        assertThat(request.findFormParameter("password")).contains("비밀");
    }

    @Test
    void 요청에서_이름으로_쿠키를_찾는다() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=session-id",
                "",
                ""
        );
        var input = new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.readFrom(input);

        assertThat(request.findCookie("JSESSIONID"))
                .contains("session-id");
    }

    @Test
    void 본문이_Content_Length보다_짧으면_예외가_발생한다() {
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 100",
                "",
                "short"
        );
        var input = new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8)
        );

        assertThatThrownBy(() -> HttpRequest.readFrom(input))
                .isInstanceOf(EOFException.class);
    }

    @Test
    void 연결된_세션을_제공한다() throws Exception {
        String rawRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                ""
        );
        var request = request(rawRequest);

        Session session = new Session("session-id");
        request.attachSession(session);

        assertThat(request.session()).isSameAs(session);
    }

    @Test
    void 세션이_연결되지_않으면_세션을_제공할_수_없다() throws Exception {
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                ""
        );
        var request = request(rawRequest);

        assertThatThrownBy(request::session)
                .isInstanceOf(IllegalStateException.class);
    }

    private HttpRequest request(final String value) throws Exception {
        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        value.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
