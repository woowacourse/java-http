package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomeControllerTest {

    @DisplayName("응답 반환 성공 : GET일 경우")
    @Test
    void service_get() throws Exception {
        // given
        HomeController controller = new HomeController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("GET", null, requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isNotNull();
    }

    @DisplayName("응답 반환 실패 : DELETE일 경우")
    @Test
    void service_notSupportedMethod_delete() throws Exception {
        // given
        HomeController controller = new HomeController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("DELETE", null, requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method not supported");
    }
}
