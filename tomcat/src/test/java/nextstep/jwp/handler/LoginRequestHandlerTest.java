package nextstep.jwp.handler;

import static org.apache.coyote.http11.session.SessionManager.findSession;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.Body;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
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
                .startLine(StartLine.from("POST /dd HTTP/1.1"))
                .build();
        HttpRequest onlyLogin = HttpRequest.builder()
                .startLine(StartLine.from("GET /login HTTP/1.1"))
                .build();
        HttpRequest match = HttpRequest.builder()
                .startLine(StartLine.from("POST /login HTTP/1.1"))
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
                .startLine(StartLine.from("POST /login HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                                "Content-Type: application/x-www-form-urlencoded",
                                "Content-Length: 30"
                        ))
                ).body(new Body("account=gugu&password=password"))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        String actual = response.toString();
        assertThat(actual).contains("HTTP/1.1 302 FOUND \r\n");
        assertThat(actual).contains("Location: /index.html \r\n");
        assertThat(actual).contains("Set-Cookie: JSESSIONID=");
        String id = response.cookies().get("JSESSIONID");
        assertThat(findSession(id)).isNotNull();
    }

    @Nested
    class 로그인에_실패하면_401_html로_redirect {

        @Test
        void 계정정보가_잘못된_경우() {
            // given
            HttpRequest request = HttpRequest.builder()
                    .startLine(StartLine.from("POST /login HTTP/1.1"))
                    .headers(RequestHeaders.from(List.of(
                                    "Content-Type: application/x-www-form-urlencoded",
                                    "Content-Length: 27"
                            ))
                    ).body(new Body("account=gugu&password=wrong"))
                    .build();

            // when
            HttpResponse response = handler.handle(request);

            // then
            var expected = "HTTP/1.1 302 FOUND \r\n" +
                    "Location: /401.html \r\n" +
                    "\r\n";
            assertThat(response.toString()).isEqualTo(expected);
            ;
        }

        @Test
        void 로그인_정보가_없는_경우() {
            // given
            HttpRequest request = HttpRequest.builder()
                    .startLine(StartLine.from("POST /login HTTP/1.1"))
                    .headers(RequestHeaders.from(List.of(
                                    "Content-Type: application/x-www-form-urlencoded",
                                    "Content-Length: 14"
                            ))
                    ).body(new Body("password=wrong"))
                    .build();

            // when
            HttpResponse response = handler.handle(request);

            // then
            var expected = "HTTP/1.1 302 FOUND \r\n" +
                    "Location: /401.html \r\n" +
                    "\r\n";
            assertThat(response.toString()).isEqualTo(expected);
        }
    }
}
