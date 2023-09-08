package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StaticResource;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoginControllerTest {
    private final Controller loginController = new LoginController();

    @Test
    @DisplayName("Reqeust를 해당 컨트롤러가 처리할 수 있다.")
    void handleTest() throws IOException {
        // given
        final String getRawRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader getStringReader = new StringReader(getRawRequest);
        final HttpRequest getRequest = HttpRequest.from(new BufferedReader(getStringReader));

        final String requestBody = "account=hubk&password=hubkpassword";
        final String postRawRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postStringReader = new StringReader(postRawRequest);
        final HttpRequest postRequest = HttpRequest.from(new BufferedReader(postStringReader));

        //when, then
        assertAll(
                () -> assertThat(loginController.canHandle(getRequest)).isTrue(),
                () -> assertThat(loginController.canHandle(postRequest)).isTrue(),
                () -> assertThat(postRequest.getRequestBody().getParamValue("account")).isEqualTo("hubk"),
                () -> assertThat(postRequest.getRequestBody().getParamValue("password")).isEqualTo("hubkpassword")
        );
    }

    @Test
    @DisplayName("지원하지 않는 HttpMethod라면 해당 컨트롤러가 처리할 수 없다.")
    void handleExceptionTest() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "DELETE /login HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        final HttpRequest getRequest = HttpRequest.from(new BufferedReader(stringReader));

        //when, then
        assertThatThrownBy(() -> loginController.service(getRequest))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("GET 요청시 login.html을 반환한다.")
    void getLogin() throws Exception {
        // given
        final String getRawRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader getStringReader = new StringReader(getRawRequest);
        final HttpRequest getRequest = HttpRequest.from(new BufferedReader(getStringReader));

        //when
        final HttpResponse response = loginController.service(getRequest);

        //then
        final StaticResource staticResource = StaticResource.from("/login.html");
        final String content = staticResource.getContent();
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        assertThat(expected).isEqualTo(response.toString());
    }

    @Test
    @DisplayName("GET 요청시 이미 로그인을 한 유저의 경우 login.html을 반환한다.")
    void getRedirect() throws Exception {
        // given
        final String requestBody = "account=gugu&password=password";
        final String postRawRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postStringReader = new StringReader(postRawRequest);
        final HttpRequest postRequest = HttpRequest.from(new BufferedReader(postStringReader));
        final HttpResponse postResponse = loginController.service(postRequest);

        //when
        final String getRawRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Content-Type: text/html ",
                "Cookie: " + postResponse.getCookieValue() + " ",
                "",
                "");
        final StringReader getStringReader = new StringReader(getRawRequest);
        final HttpRequest getRequest = HttpRequest.from(new BufferedReader(getStringReader));
        final HttpResponse getResponse = loginController.service(getRequest);

        //then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");
        assertThat(getResponse.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST 요청시 등록되지 않은 사용자는 401.html으로 리다이렉트한다.")
    void loginFailRedirect() throws Exception {
        // given
        final String requestBody = "account=hubk&password=hubkpassword";
        final String postRawRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postStringReader = new StringReader(postRawRequest);
        final HttpRequest postRequest = HttpRequest.from(new BufferedReader(postStringReader));

        //when
        final HttpResponse response = loginController.service(postRequest);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /401.html ",
                "",
                "");
        assertThat(response.toString()).isEqualTo(expected);
    }

    @DisplayName("POST 요청시 등록된 사용자는 로그인처리 후 index.html으로 리다이렉트한다.")
    @Test
    void loginSuccessRedirect() throws Exception {
        // given
        final String requestBody = "account=gugu&password=password";
        final String postRawRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postStringReader = new StringReader(postRawRequest);
        final HttpRequest postRequest = HttpRequest.from(new BufferedReader(postStringReader));

        //when
        final HttpResponse response = loginController.service(postRequest);
        final String cookieValue = response.getCookieValue();

        // then
        final StaticResource staticResource = StaticResource.from("/index.html");
        final String content = staticResource.getContent();
        final Session session = SessionManager.findSession(cookieValue.split("=")[1]);
        final User user = InMemoryUserRepository.findByAccount("gugu").get();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "Set-Cookie: " + cookieValue + " ",
                "",
                "");

        assertAll(
                () -> assertThat(response.toString()).isEqualTo(expected),
                () -> assertThat(session.getAttribute("user")).isEqualTo(user)
        );
    }
}
