package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ControllerTest {

    @Test
    void 올바른_로그인_요청일_경우_index_화면을_응답한다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET /login?account=gugu&password=password HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when
        ResponseEntity response = Controller.processRequest(request);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("redirect:index.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.REDIRECT)
        );

    }

    @Test
    void 로그인_페이지_요청일_경우_login_화면을_응답한다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET /login HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when
        ResponseEntity response = Controller.processRequest(request);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("login.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 로그인_시_계정이_없는_요청일_경우_예외를_던진다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET /login?account=eden&password=password HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않는_요청일_경우_예외를_던진다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET /login?account=gugu&password=gugugugu HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(request))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void home_요청일_경우_hello_world를_반환한다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET / HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when
        ResponseEntity response = Controller.processRequest(request);

        // then
        assertThat(response.getResponseBody()).isEqualTo("Hello world!");
    }

    @Test
    void 없는_api_요청일_경우_예외를_반환한다() throws URISyntaxException, IOException {
        // given
        String rawStartLine = "GET /eden HTTP/1.1";
        StartLine startLine = new StartLine(rawStartLine);
        Request request = Request.of(startLine);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
