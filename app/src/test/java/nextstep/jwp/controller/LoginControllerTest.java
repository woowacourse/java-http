package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.mapper.ControllerMapper;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
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

    @Test
    void doPostWithValidLoginInfo() throws IOException {
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

        // when
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 싪패했습니다");
    }

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

        // when
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("해당 아이디가 없습니다");
    }
}
