package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageControllerTest {

    @DisplayName("응답 반환 성공 : GET일 경우")
    @Test
    void service_get() throws Exception {
        // given
        PageController controller = new PageController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("GET", "/index.html", requestHeader, null);
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
        PageController controller = new PageController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("DELETE", null, requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method not supported");
    }

    @DisplayName("GET 응답 반환 성공 : 유효한 파일일 경우")
    @Test
    void service_get_validPath() throws Exception {
        // given
        PageController controller = new PageController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("GET", "/index.html", requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
    }

    @DisplayName("GET 응답 반환 실패 : 유효하지 않은 파일일 경우")
    @Test
    void service_get_invalidPath_exception() throws Exception {
        // given
        PageController controller = new PageController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(null, null, null);
        HttpRequest request = new HttpRequest("GET", "/i.html", requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid static");
    }

}
