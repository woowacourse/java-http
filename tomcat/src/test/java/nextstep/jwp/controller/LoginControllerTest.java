package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @Test
    void queryString이_존재하지_않는다면_로그인_페이지를_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/login");
        final Request request = new Request(requestInfo, new Headers(), null);
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_LENGTH, "3796");
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        final Response expected = new Response(headers);

        // when
        final Response actual = controller.execute(request);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expected);
    }

    @Test
    void account값과_password값이_일치하면_index로_리다이렉트한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/login", "account=gugu&password=password");
        final Request request = new Request(requestInfo, new Headers(), null);
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, "/index.html");
        final Response expected = new Response(headers).httpStatus(HttpStatus.FOUND);

        // when
        final Response actual = controller.execute(request);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void account값이_일치하지_않으면_UNAUTHORIZED_예외를_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/login", "account=gonggong&password=password");
        final Request request = new Request(requestInfo, new Headers(), null);

        // when, then
        assertThatThrownBy(() -> controller.execute(request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void paasword값이_일치하지_않으면_UNAUTHORIZED_예외를_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/login", "account=gugu&password=password1");
        final Request request = new Request(requestInfo, new Headers(), null);

        // when, then
        assertThatThrownBy(() -> controller.execute(request))
                .isInstanceOf(UnauthorizedException.class);
    }
}
