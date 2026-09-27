package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void GET_요청은_GET_처리로_분기한다() throws Exception {
        HttpRequest request = request("GET /test HTTP/1.1");
        HttpResponse response = new HttpResponse();
        Controller controller = new TestController();

        controller.service(request, response);

        assertThat(responseText(response))
                .contains("Location: /get");
    }

    @Test
    void POST_요청은_POST_처리로_분기한다() throws Exception {
        HttpRequest request = request("POST /test HTTP/1.1");
        HttpResponse response = new HttpResponse();
        Controller controller = new TestController();

        controller.service(request, response);

        assertThat(responseText(response))
                .contains("Location: /post");
    }

    private HttpRequest request(final String requestLine) throws Exception {
        String rawRequest = String.join("\r\n",
                requestLine,
                "",
                ""
        );

        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        rawRequest.getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private String responseText(final HttpResponse response) {
        return new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    private static class TestController extends AbstractController {

        @Override
        protected void doGet(
                final HttpRequest request,
                final HttpResponse response
        ) {
            response.sendRedirect("/get");
        }

        @Override
        protected void doPost(
                final HttpRequest request,
                final HttpResponse response
        ) {
            response.sendRedirect("/post");
        }
    }
}
