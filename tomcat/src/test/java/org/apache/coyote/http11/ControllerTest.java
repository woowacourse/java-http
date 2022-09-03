package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

class ControllerTest {

    @Test
    void 올바른_로그인_요청을_처리한다() {
        // given
        String url = "/login?account=gugu&password=password";
        QueryParameters queryParameters = QueryParameters.of(url);

        // when
        String response = Controller.processRequest("/login", queryParameters);

        // then
        assertThat(response).isEqualTo("login.html");
    }

    @Test
    void 로그인_시_계정이_없는_요청일_경우_예외를_던진다() {
        // given
        String url = "/login?account=eden&password=password";
        QueryParameters queryParameters = QueryParameters.of(url);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest("/login", queryParameters))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않는_요청일_경우_예외를_던진다() {
        // given
        String url = "/login?account=gugu&password=gugugugu";
        QueryParameters queryParameters = QueryParameters.of(url);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest("/login", queryParameters))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void home_요청일_경우_hello_world를_반환한다() {
        // given
        String url = "/";
        QueryParameters queryParameters = QueryParameters.of(url);

        // when
        String response = Controller.processRequest("/", queryParameters);

        // then
        assertThat(response).isEqualTo("Hello world!");
    }

    @Test
    void 없는_api_요청일_경우_예외를_반환한다() {
        // given
        String url = "/eden";
        QueryParameters queryParameters = QueryParameters.of(url);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(url, queryParameters))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
