package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    @DisplayName("응답 반환 성공 : GET일 경우")
    @Test
    void service_get() throws Exception {
        // given
        HomeController controller = new HomeController();
        HttpRequest request = new HttpRequest("GET", null, null, null, null, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getResponseBody()).isNotNull();
    }

    @DisplayName("GET 응답 반환 실패 : DELETE일 경우")
    @Test
    void service_notSupportedMethod_delete() throws Exception {
        // given
        HomeController controller = new HomeController();
        HttpRequest request = new HttpRequest("DELETE", null, null, null, null, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method not supported");
    }
}
