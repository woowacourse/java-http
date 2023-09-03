package nextstep.jwp;

import static nextstep.jwp.HttpStatus.FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import support.StubSocket;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestHandlerTest {

    @Test
    void html_요청이_오면_content_type은_html() {
        // given
        String request = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(request);

        // then
        assertThat(response).contains("text/html");
    }

    @Test
    void css_요청이_오면_content_type은_css() throws IOException {
        // given
        String request = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(request);

        // then
        assertThat(response).contains("text/css");
    }

    @Test
    void js_요청이_오면_content_type은_js() {
        // given
        String request = String.join("\r\n",
            "GET /js/scripts.js HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(request);

        // then
        assertThat(response).contains("application/javascript");
    }

    @Test
    void 정상적으로_로그인_성공() {
        // given
        String httpRequest = String.join("\r\n",
            "GET /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(httpRequest);

        // then
        assertAll(
            () -> assertThat(response).contains(FOUND.name()),
            () -> assertThat(response).contains("Location: /index.html")
        );
    }

    @Test
    void 존재하지_않는_아이디로_로그인_요청() {
        // given
        String httpRequest = String.join("\r\n",
            "GET /login?account=noUser&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(httpRequest);

        // then
        assertAll(
            () -> assertThat(response).contains(FOUND.name()),
            () -> assertThat(response).contains("Location: /401.html")
        );
    }

    @Test
    void 잘못된_비밀번호로_로그인_요청() {
        // given
        String httpRequest = String.join("\r\n",
            "GET /login?account=gugu&password=invalidPassword HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        String response = HTTP_요청을_보낸다(httpRequest);

        // then
        assertAll(
            () -> assertThat(response).contains(FOUND.name()),
            () -> assertThat(response).contains("Location: /401.html")
        );
    }
    
    @Test
    void 회원가입() {
        // given
        
        // when
        
        // then
    }

    private String HTTP_요청을_보낸다(String request) {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        try {
            return RequestHandler.handle(new HttpRequest(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
