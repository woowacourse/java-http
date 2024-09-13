package com.techcourse.handler;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginControllerTest {

    @Test
    @DisplayName("GET 요청을 처리할 수 있다.")
    void doGet() throws Exception {
        LoginController loginController = new LoginController();
        HttpResponse response = HttpResponse.createHttp11Response();

        loginController.doGet(mock(HttpRequest.class), response);

        Assertions.assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @Test
    @DisplayName("POST 요청을 처리할 수 있다. (로그인 처리)")
    void doPost() throws Exception {
        LoginController loginController = new LoginController();
        HttpResponse response = HttpResponse.createHttp11Response();
        SimpleBody body = createBody("gugu", "password");
        Manager manager = mock(Manager.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getId())
                .thenReturn("1234");
        when(manager.findSession(any()))
                .thenReturn(session);

        HttpRequest request = HttpRequest.createHttp11Request(
                "POST /login HTTP/1.1",
                Header.empty(),
                body,
                manager
        );

        loginController.doPost(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader().get(HttpHeaderKey.LOCATION)).hasValue("index.html");
        verify(session, atLeastOnce()).setAttribute(eq("user"), any());
    }

    @Test
    @DisplayName("로그인 실패시 401.html로 리다이렉션 처리한다.")
    void doPostWithLoginFail() throws Exception {
        LoginController loginController = new LoginController();
        HttpResponse response = HttpResponse.createHttp11Response();
        SimpleBody body = createBody("gugu", "wrongPassword");

        HttpRequest request = HttpRequest.createHttp11Request(
                "POST /login HTTP/1.1",
                Header.empty(),
                body,
                mock(Manager.class)
        );

        loginController.doPost(request, response);

        Assertions.assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeader().get(HttpHeaderKey.LOCATION)).hasValue("401.html")
        );
    }

    @Test
    @DisplayName("account는 필수 값이다.")
    void requireAccount() throws Exception {
        LoginController loginController = new LoginController();
        HttpResponse response = HttpResponse.createHttp11Response();
        SimpleBody body = createBody(null, "1234");

        HttpRequest request = HttpRequest.createHttp11Request(
                "POST /login HTTP/1.1",
                Header.empty(),
                body,
                Mockito.mock(Manager.class)
        );

        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account 값은 필수입니다.");
    }

    @Test
    @DisplayName("password는 필수 값이다.")
    void requirePassword() throws Exception {
        LoginController loginController = new LoginController();
        HttpResponse response = HttpResponse.createHttp11Response();
        SimpleBody body = createBody("lee", null);

        HttpRequest request = HttpRequest.createHttp11Request(
                "POST /login HTTP/1.1",
                Header.empty(),
                body,
                Mockito.mock(Manager.class)
        );

        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password 값은 필수입니다.");
    }

    private SimpleBody createBody(String account, String password) {
        HashMap<String, String> data = new HashMap<>();
        data.put("account", account);
        data.put("password", password);

        return new SimpleBody(data);
    }
}
