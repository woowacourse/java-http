package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void GET_요청은_컨트롤러의_doGet에서_처리한다() throws Exception {
        // given
        AbstractController controller = new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                response.sendRedirect("/get");
            }
        };

        // when
        String response = service(controller, "GET");

        // then
        assertThat(response).startsWith("HTTP/1.1 302 Found").contains("Location: /get");
    }

    @Test
    void POST_요청은_컨트롤러의_doPost에서_처리한다() throws Exception {
        // given
        AbstractController controller = new AbstractController() {
            @Override
            protected void doPost(HttpRequest request, HttpResponse response) {
                response.sendRedirect("/post");
            }
        };

        // when
        String response = service(controller, "POST");

        // then
        assertThat(response).startsWith("HTTP/1.1 302 Found").contains("Location: /post");
    }

    @Test
    void 구현하지_않은_요청_메서드에는_404_페이지를_응답한다() throws Exception {
        // given
        AbstractController controller = new AbstractController() {
        };

        // when
        String response = service(controller, "PATCH");

        // then
        assertThat(response).startsWith("HTTP/1.1 404 Not Found")
                .contains("Content-Type: text/html;charset=utf-8");
    }

    @Test
    void doPost가_없는_컨트롤러에_POST를_보내면_404를_응답한다() throws Exception {
        // given
        AbstractController controller = new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                response.sendRedirect("/get");
            }
        };

        // when
        String response = service(controller, "POST");

        // then
        assertThat(response).startsWith("HTTP/1.1 404 Not Found");
    }

    private String service(Controller controller, String method) throws Exception {
        String raw = method + " /example HTTP/1.1\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = new HttpRequestParser(input).parse();
        HttpResponse response = new HttpResponse();
        controller.service(request, response);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.writeTo(output);
        return output.toString(StandardCharsets.UTF_8);
    }
}
