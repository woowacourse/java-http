package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestHeaders;
import org.apache.catalina.servlet.request.StartLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.session.Session;
import org.apache.catalina.servlet.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LoginPageHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LoginPageHandlerTest {

    private final LoginPageHandler handler = new LoginPageHandler();

    @Test
    void GET이면서_login_으로_들어온_요청만_처리한다() {
        // given
        HttpRequest onlyGet = HttpRequest.builder()
                .startLine(StartLine.from("GET /dd HTTP/1.1"))
                .build();
        HttpRequest onlyLogin = HttpRequest.builder()
                .startLine(StartLine.from("POST /login HTTP/1.1"))
                .build();
        HttpRequest match = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1"))
                .build();

        // when & then
        assertThat(handler.canHandle(onlyGet)).isFalse();
        assertThat(handler.canHandle(onlyLogin)).isFalse();
        assertThat(handler.canHandle(match)).isTrue();
    }

    @Test
    void 세션정보가_존재하면_index로_redirect_시킨다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                        "Cookie: JSESSIONID=" + session.id()
                )))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        assertThat(response.statusLine().httpStatus()).isEqualTo(FOUND);
        assertThat(response.headers().headers().get("Location")).isEqualTo("/index.html");
    }

    @Test
    void 쿠키는_있으나_세션정보가_없다면_login_화면을_보여준다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                        "Cookie: JSESSIONID=" + "1234"
                )))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3447 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(response.toString()).isEqualTo(expected);
    }

    @Test
    void 세션정보는_있으나_쿠키가_없으면_login_화면을_보여준다() throws IOException {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1"))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3447 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(response.toString()).isEqualTo(expected);
    }
}
