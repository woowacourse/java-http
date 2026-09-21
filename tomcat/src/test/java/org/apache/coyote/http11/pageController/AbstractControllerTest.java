package org.apache.coyote.http11.pageController;

import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {
    private final SessionManager sessionManager = new SessionManager();

    @Test
    void notImplementedMethodIsNotAllowed() throws IOException {
        // given
        final PageController controller = new AbstractController() {
        };
        final HttpRequest request = request("POST /login HTTP/1.1", HttpHeaders.empty(), HttpBody.empty());

        // when
        final HttpResponse response = controller.run(request);

        // then
        final String actual = new String(response.toBytes(), StandardCharsets.UTF_8);
        assertThat(actual)
                .startsWith("HTTP/1.1 405 Method Not Allowed ")
                .endsWith("지원하지 않는 HTTP 메서드입니다: POST");
    }
    private HttpRequest request(String requestLine, HttpHeaders headers, HttpBody body) {
        return HttpRequest.from(requestLine, headers, body, sessionManager);
    }

}
