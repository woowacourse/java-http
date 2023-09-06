package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.apache.catalina.servlet.session.SessionConstant.JSESSIONID;
import static org.apache.catalina.servlet.session.SessionManager.findSession;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.catalina.servlet.request.Body;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestHeaders;
import org.apache.catalina.servlet.request.RequestLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LoginRequestHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LoginRequestHandlerTest {

    private final LoginRequestHandler handler = new LoginRequestHandler();

    @Test
    void POST_이면서_login으로_들어온_요청만_처리한다() {
        // given
        HttpRequest onlyPost = HttpRequest.builder()
                .requestLine(RequestLine.from("POST /dd HTTP/1.1"))
                .build();
        HttpRequest onlyLogin = HttpRequest.builder()
                .requestLine(RequestLine.from("GET /login HTTP/1.1"))
                .build();
        HttpRequest match = HttpRequest.builder()
                .requestLine(RequestLine.from("POST /login HTTP/1.1"))
                .build();

        // when & then
        assertThat(handler.canHandle(onlyPost)).isFalse();
        assertThat(handler.canHandle(onlyLogin)).isFalse();
        assertThat(handler.canHandle(match)).isTrue();
    }

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
        handler.handle(request, response);

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
            handler.handle(request, response);

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
            handler.handle(request, response);

            // then
            HttpResponse expected = new HttpResponse();
            expected.setStatusLine(new StatusLine(FOUND));
            expected.addHeader("Location", "/401.html");
            assertThat(response).usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }
}
