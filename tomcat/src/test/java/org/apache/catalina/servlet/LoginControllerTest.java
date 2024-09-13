package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.catalina.servlet.controller.LoginController;
import org.apache.support.Dummy;
import org.apache.tomcat.http.common.body.FormUrlEncodeBody;
import org.apache.tomcat.http.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("post 요청 시 쿠키를 반환받는다.")
    void post_request_then_cookie_response() {
        // given
        final var controller = new LoginController();
        final var request = Dummy.getHttpRequestBody(
                new FormUrlEncodeBody("account=gugu&password=password"));
        final var response = Dummy.getHttpResponse();

        // when
        controller.doPost(request, response);

        // then
        assertThat(response.getHeaderContent("Set-Cookie")).isNotBlank();
    }

    @Test
    @DisplayName("로그인이 된 상태로 GET 요청을 하면 index.html을 반환한다.")
    void return_index_dot_html_when_called_get_request_with_valid_jsession_id() {
        // given
        final var controller = new LoginController();
        final var request = Dummy.getHttpRequestBody(
                new FormUrlEncodeBody("account=gugu&password=password"));
        final var response = Dummy.getHttpResponse();
        controller.doPost(request, response);
        final var cookie = response.getHeaderContent("Set-Cookie");

        // when
        controller.doGet(Dummy.getHttpRequestCookie(cookie), response);

        // then
        assertThat(response.getHeaderContent("Location")).isEqualTo("http://localhost:8080/index.html");
    }

    @Test
    @DisplayName("로그인 되지 않은 상태에서 GET요청을 할시 예외를 던진다.")
    void throw_exception_when_called_get_request_without_valid_jsession_id() {
        // given
        final var controller = new LoginController();
        final var request = Dummy.getHttpRequestBody(
                new FormUrlEncodeBody("account=gugu&password=password1"));
        final var response = Dummy.getHttpResponse();

        // when & then
        assertThatThrownBy(() -> controller.doPost(request, response))
                .isInstanceOf(AuthenticationException.class);
    }
}
