package org.apache.coyote.httpresponse.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class RegisterHandlerTest extends HandlerTestSupport {

    @Test
    void 회원가입을_완료_하면_index_페이지로_리다이렉트_한다() {
        // given
        final String account = "ditoo";
        final String password = "1234";
        final String email = "ditoo@naver.com";

        final String input = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "Content-Length: 51",
                "",
                String.format("account=%s&password=%s&email=%s", account, password, email));
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final RegisterHandler registerHandler = new RegisterHandler();

        // when
        final HttpResponse httpResponse = registerHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 302 Found",
                "Location: /index.html");

        // then
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        assertSoftly(softly -> {
            assertThat(actual).contains(expectedHeaders);
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().checkPassword(password)).isTrue();
        });
    }

    @Test
    void 회원가입_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: text/html;charset=utf-8 ",
                "Accept: */* ",
                "");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final RegisterHandler registerHandler = new RegisterHandler();

        // when
        final HttpResponse httpResponse = registerHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ");

        // then
        assertThat(actual).contains(expectedHeaders);
    }

    @Test
    void 지원하지_않는_메소드로_회원가입_페이지를_요청하면_405_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "PUT /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: text/html;charset=utf-8 ",
                "Accept: */* ",
                "");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final RegisterHandler registerHandler = new RegisterHandler();

        // when
        final HttpResponse httpResponse = registerHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 405 Method Not Allowed",
                "Content-Type: text/html;charset=utf-8 ");

        // then
        assertThat(actual).contains(expectedHeaders);
    }
}
