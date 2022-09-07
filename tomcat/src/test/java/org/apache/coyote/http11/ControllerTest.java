package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ControllerTest {

    @Test
    void 올바른_로그인_요청일_경우_index_화면을_응답한다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=gugu&password=password");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity response = Controller.processRequest(httpRequest);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("redirect:index.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.REDIRECT)
        );

    }

    @Test
    void 로그인_페이지_요청일_경우_login_화면을_응답한다() {
        // given
        StartLine startLine = new StartLine("GET /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity response = Controller.processRequest(httpRequest);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("login.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 로그인_시_계정이_없는_요청일_경우_예외를_던진다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=eden&password=password");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity response = Controller.processRequest(httpRequest);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("redirect:401.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.REDIRECT)
        );
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않는_요청일_경우_예외를_던진다() {
        // given
        StartLine startLine = new StartLine("POST /login HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=gugu&password=gugugugu");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity response = Controller.processRequest(httpRequest);

        // then
        Assertions.assertAll(
                () -> assertThat(response.getResponseBody()).isEqualTo("redirect:401.html"),
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.REDIRECT)
        );
    }

    @Test
    void home_요청일_경우_hello_world를_반환한다() {
        // given
        StartLine startLine = new StartLine("GET / HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity response = Controller.processRequest(httpRequest);

        // then
        assertThat(response.getResponseBody()).isEqualTo("Hello world!");
    }

    @Test
    void 없는_api_path_요청일_경우_예외를_반환한다() {
        // given
        StartLine startLine = new StartLine("GET /eden HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(httpRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void POST_요청을_찾을_수_있다() {
        // given
        StartLine startLine = new StartLine("POST /register HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("account=eden&email=eden@morak.com&password=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> Controller.processRequest(httpRequest));
    }

    @Test
    void 없는_api_method_요청일_경우_예외를_반환한다() {
        // given
        StartLine startLine = new StartLine("POST / HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when & then
        assertThatThrownBy(() -> Controller.processRequest(httpRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 회원가입을_하면_index_html을_반환한다() {
        // given
        StartLine startLine = new StartLine("POST /register HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Type: text/html"));
        RequestBody requestBody = RequestBody.of("account=eden&email=eden@morak.com&password=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // when
        ResponseEntity responseEntity = Controller.processRequest(httpRequest);

        // then
        Assertions.assertAll(
                () -> assertThat(responseEntity.getResponseBody()).isEqualTo("redirect:index.html"),
                () -> assertThat(responseEntity.getHttpStatus()).isEqualTo(HttpStatus.REDIRECT)
        );
    }
}
