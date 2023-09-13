package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.apache.catalina.servlet.session.SessionConstant.JSESSIONID;
import static org.apache.catalina.servlet.session.SessionManager.findSession;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.servlet.request.Body;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestHeaders;
import org.apache.catalina.servlet.request.RequestLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.session.Session;
import org.apache.catalina.servlet.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LoginHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LoginHandlerTest {

    private final LoginHandler handler = new LoginHandler();

    @Test
    void 로그인에_성공하면_session_쿠키를_제공하고_index_html로_redirect() {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("POST /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                                "Content-Type: application/x-www-form-urlencoded",
                                "Content-Length: 30"
                        ))
                ).body(new Body("account=gugu&password=password"))
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.service(request, response);

        // then
        String id = response.cookies().get("JSESSIONID");

        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(FOUND));
        expected.addHeader("Location", "/index.html");
        expected.addCookie(JSESSIONID, id);
        assertThat(findSession(id)).isNotNull();
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Nested
    class 로그인에_실패하면_401_html로_redirect {

        @Test
        void 계정정보가_잘못된_경우() {
            // given
            HttpRequest request = HttpRequest.builder()
                    .requestLine(RequestLine.from("POST /login HTTP/1.1"))
                    .headers(RequestHeaders.from(List.of(
                                    "Content-Type: application/x-www-form-urlencoded",
                                    "Content-Length: 27"
                            ))
                    ).body(new Body("account=gugu&password=wrong"))
                    .build();
            HttpResponse response = new HttpResponse();

            // when
            handler.service(request, response);

            // then
            HttpResponse expected = new HttpResponse();
            expected.setStatusLine(new StatusLine(FOUND));
            expected.addHeader("Location", "/401.html");
            assertThat(response).usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @Test
        void 로그인_정보가_없는_경우() {
            // given
            HttpRequest request = HttpRequest.builder()
                    .requestLine(RequestLine.from("POST /login HTTP/1.1"))
                    .headers(RequestHeaders.from(List.of(
                                    "Content-Type: application/x-www-form-urlencoded",
                                    "Content-Length: 14"
                            ))
                    ).body(new Body("password=wrong"))
                    .build();
            HttpResponse response = new HttpResponse();

            // when
            handler.service(request, response);

            // then
            HttpResponse expected = new HttpResponse();
            expected.setStatusLine(new StatusLine(FOUND));
            expected.addHeader("Location", "/401.html");
            assertThat(response).usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }

    @Test
    void 세션정보가_존재하면_index로_redirect_시킨다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                        "Cookie: JSESSIONID=" + session.id()
                )))
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.service(request, response);

        // then
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.FOUND));
        expected.addHeader("Location", "/index.html");
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 쿠키는_있으나_세션정보가_없다면_login_화면을_보여준다() throws IOException {
        // given
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                        "Cookie: JSESSIONID=" + "1234"
                )))
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/html;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 세션정보는_있으나_쿠키가_없으면_login_화면을_보여준다() throws IOException {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        HttpRequest request = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /login HTTP/1.1"))
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("Content-Type", "text/html;charset=utf-8");
        expected.setMessageBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
