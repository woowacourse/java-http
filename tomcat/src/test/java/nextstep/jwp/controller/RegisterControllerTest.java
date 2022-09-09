package nextstep.jwp.controller;

import org.apache.coyote.Headers;
import nextstep.jwp.http.MockOutputStream;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestInfo;
import org.apache.coyote.support.Response;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.HttpHeader.LOCATION;
import static org.apache.coyote.HttpMethod.POST;
import static org.apache.coyote.HttpStatus.FOUND;
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
        final Response response = new Response(new MockOutputStream());

        final Response expected = new Response(new MockOutputStream()).header(LOCATION, "/index.html")
                .httpStatus(FOUND);

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
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
        final Response response = new Response(new MockOutputStream());

        final Response expected = new Response(new MockOutputStream()).header(LOCATION, "/register")
                .httpStatus(FOUND);

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
