package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.servlet.controller.LoginController;
import org.apache.support.Dummy;
import org.apache.tomcat.http.common.body.FormUrlEncodeBody;
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

}
