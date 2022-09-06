package nextstep.jwp.controller;

import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpHeader.LOCATION;
import static org.apache.http.HttpMethod.POST;
import static org.apache.http.HttpStatus.FOUND;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @Test
    void 회원가입에_성공하면_index로_리다이렉트_한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/register");
        final String body = String.join("&",
                "account=corinne",
                "email=yoo77hyeon@gmail.com",
                "password=password");
        final Request request = new Request(requestInfo, new Headers(), body);

        final Headers headers = new Headers();
        headers.put(LOCATION, "/index.html");
        final Response expected = new Response(headers).httpStatus(FOUND);

        // when
        final Response actual = controller.execute(request);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 필수내용을_입력하지_않으면_회원가입_페이지로_리다이렉트_한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/register");
        final String body = String.join("&",
                "account=",
                "email=yoo77hyeon@gmail.com",
                "password=1234");
        final Request request = new Request(requestInfo, new Headers(), body);

        final Headers headers = new Headers();
        headers.put(LOCATION, "/register");
        final Response expected = new Response(headers).httpStatus(FOUND);

        // when
        final Response actual = controller.execute(request);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
