package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void POST_회원가입_요청으로_사용자를_저장하고_리다이렉트한다() throws Exception {
        Controller controller = new RegisterController(new StaticResourceController());

        String body = "account=test&password=password&email=test@test.com";

        HttpRequest request = request(
                String.join("\r\n",
                        "POST /register HTTP/1.1",
                        "Content-Type: application/x-www-form-urlencoded",
                        "Content-Length: "
                                + body.getBytes(StandardCharsets.UTF_8).length,
                        "",
                        body
                )
        );

        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(InMemoryUserRepository.findByAccount("test"))
                .isPresent();

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).contains("Location: /index.html");
    }

    @Test
    void GET_회원가입_요청에_회원가입_페이지를_응답한다() throws Exception {
        Controller controller = new RegisterController(new StaticResourceController());

        HttpRequest request = request(
                "GET /register HTTP/1.1\r\n\r\n"
        );

        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).contains("Content-Type: text/html");
        assertThat(actual).doesNotEndWith("Hello world!");
    }

    private HttpRequest request(final String value) throws Exception {
        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        value.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
