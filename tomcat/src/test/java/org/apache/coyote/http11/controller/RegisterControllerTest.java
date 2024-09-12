package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    @DisplayName("응답 반환 성공 : GET일 경우")
    @Test
    void service_get() throws Exception {
        // given
        RegisterController controller = new RegisterController();
        HttpRequest request = new HttpRequest("GET", null, null, null, null, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isNotNull();
    }

    @DisplayName("응답 반환 성공 : POST일 경우")
    @Test
    void service_post() throws Exception {
        // given
        RegisterController controller = new RegisterController();
        Map<String, List<String>> body = Map.of("account", List.of("gugu2"), "password", List.of("password"), "email", List.of("gugu2@gmail.com"));
        HttpRequest request = new HttpRequest("POST", null, null, null, null, body);
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
        RegisterController controller = new RegisterController();
        HttpRequest request = new HttpRequest("DELETE", null, Map.of("key", "value"), null, null, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method not supported");
    }

    @DisplayName("GET 응답 반환 성공")
    @Test
    void service_get_OK() throws Exception {
        // given
        RegisterController controller = new RegisterController();
        HttpRequest request = new HttpRequest("GET", null, null, null, null, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
    }

    @DisplayName("POST 응답 반환 성공")
    @Test
    void service_post_found() throws Exception {
        // given
        RegisterController controller = new RegisterController();
        Map<String, List<String>> body = Map.of("account", List.of("gugu2"), "password", List.of("password"), "email", List.of("gugu2@gmail.com"));
        HttpRequest request = new HttpRequest("POST", null, null, null, null, body);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
    }

}
