package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    @DisplayName("응답 반환 성공 : GET일 경우")
    @Test
    void service_get() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        HttpRequest request = new HttpRequest("GET", null, requestHeader, null);
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
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("account", List.of("gugu"), "password", List.of("password"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
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
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        HttpRequest request = new HttpRequest("DELETE", null, requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method not supported");
    }

    @DisplayName("GET 응답 반환 성공 : JSESSIONID 쿠키 없을 경우")
    @Test
    void service_get_notExistJssessionId() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        HttpRequest request = new HttpRequest("GET", null, requestHeader, null);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
    }

    @DisplayName("POST 응답 반환 성공 : 비밀번호 일치할 경우")
    @Test
    void service_post_correctPassword() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("account", List.of("gugu"), "password", List.of("password"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getPath()).isEqualTo("/index.html");
    }

    @DisplayName("POST 응답 반환 성공 : 비밀번호 일치하지 않을 경우")
    @Test
    void service_post_incorrectPassword() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("account", List.of("gugu"), "password", List.of("pasord"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getPath()).isEqualTo("/401.html");
    }

    @DisplayName("POST 응답 반환 실패 : account 없을 경우")
    @Test
    void service_post_accountNotFound_Exception() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("password", List.of("pasord"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("account not found");
    }

    @DisplayName("POST 응답 반환 실패 : user 없을 경우")
    @Test
    void service_post_userNotFound_Exception() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("account", List.of("gigi"), "password", List.of("pasord"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("user not found");
    }

    @DisplayName("POST 응답 반환 실패 : password 없을 경우")
    @Test
    void service_post_passwordNotFound_Exception() throws Exception {
        // given
        LoginController controller = new LoginController();
        HttpRequestHeader requestHeader = new HttpRequestHeader(Map.of("key", "value"), null, null);
        Map<String, List<String>> body = Map.of("account", List.of("gugu"));
        HttpRequest request = new HttpRequest("POST", null, requestHeader, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(
                () -> controller.service(request, response)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password not found");
    }

}
