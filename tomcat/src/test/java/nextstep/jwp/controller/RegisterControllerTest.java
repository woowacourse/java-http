package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @Test
    void GET_요청을_시_회원가입_페이지를_반환하도록_설정한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /register HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("register.html")
        );
    }

    @Test
    void POST_요청_시_이미_존재하는_회원인_경우_회원가입_페이지를_반환하도록_설정한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /register HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("register.html")
        );
    }

    @Test
    void POST_요청_시_회원가입에_성공하는_경우_index_페이지를_반환하도록_설정한다() {
        // given
        final RequestLine requestLine = RequestLine.from("POST /register HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("index.html")
        );
    }
}
