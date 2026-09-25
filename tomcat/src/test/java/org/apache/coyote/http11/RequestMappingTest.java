package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    @Test
    void 등록한_경로로_요청하면_해당_컨트롤러를_반환한다() throws IOException {
        // given
        RequestMapping mapping = new RequestMapping();
        Controller loginController = (request, response) -> response.sendRedirect("/index.html");
        mapping.add("/login", loginController);

        // when
        Controller found = mapping.getController(request("GET", "/login"));

        // then
        assertThat(found).isSameAs(loginController);
    }

    @Test
    void 같은_경로의_GET과_POST는_동일한_컨트롤러를_반환한다() throws IOException {
        // given
        RequestMapping mapping = new RequestMapping();
        Controller loginController = (request, response) -> response.sendRedirect("/index.html");
        mapping.add("/login", loginController);

        // when
        Controller get = mapping.getController(request("GET", "/login"));
        Controller post = mapping.getController(request("POST", "/login"));

        // then
        assertThat(get).isSameAs(loginController);
        assertThat(post).isSameAs(loginController);
    }

    @Test
    void 등록되지_않은_경로로_요청하면_컨트롤러를_반환하지_않는다() throws IOException {
        // given
        RequestMapping mapping = new RequestMapping();

        // when
        Controller found = mapping.getController(request("GET", "/missing"));

        // then
        assertThat(found).isNull();
    }

    private HttpRequest request(String method, String path) throws IOException {
        String raw = method + " " + path + " HTTP/1.1\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestParser(input).parse();
    }
}
