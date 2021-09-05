package nextstep.jwp.application.controller;

import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.mapper.ControllerMapper;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.testutils.TestHttpRequestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertContainsBodyString;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    private final ControllerMapper controllerMapper = ControllerMapper.getInstance();
    private final LoginController loginController = (LoginController) controllerMapper.resolve("/login");

    @DisplayName("GET 요청의 동작을 확인한다.")
    @Test
    void doGet() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.OK);
        assertContainsBodyString(httpResponseMessage, "로그인");
    }

    @DisplayName("POST 요청의 동작을 확인한다.")
    @Test
    void doPost() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doPost(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/index.html");
    }

    @DisplayName("비밀번호가 틀린 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithInValidPassword() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=12345");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 싪패했습니다");
    }

    @DisplayName("아이디가 없는 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithNonexistentId() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 32",
                "",
                "account=pidgey&password=password");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("해당 아이디가 없습니다");
    }

    @DisplayName("필요한 값이 없는 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithRequiredNull() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("아이디 또는 비밀번호를 입력하지 않았습니다");
    }
}
