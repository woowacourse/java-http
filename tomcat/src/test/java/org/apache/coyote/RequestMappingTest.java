package org.apache.coyote;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    private final RequestMapping requestMapping
            = new RequestMapping(SessionManager.getInstance());

    @Test
    void login_요청에_LoginController를_반환한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /login HTTP/1.1");

        // when
        final Controller controller = requestMapping.getController(request)
                .orElseThrow();

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void register_요청에_RegisterController를_반환한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /register HTTP/1.1");

        // when
        final Controller controller = requestMapping.getController(request).orElseThrow();

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void 등록되지_않은_경로면_Controller가_없다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /index.html HTTP/1.1");

        // when & then
        assertThat(requestMapping.getController(request)).isEmpty();
    }

    private HttpRequest createRequest(final String requestLine) throws Exception {

        final String rawRequest = String.join(
                "\r\n",
                requestLine,
                "Host: localhost:8080",
                "",
                ""
        );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        return HttpRequest.from(inputStream).orElseThrow();
    }
}