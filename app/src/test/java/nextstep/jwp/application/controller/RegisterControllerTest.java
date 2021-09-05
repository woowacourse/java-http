package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.model.User;
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
import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final ControllerMapper controllerMapper = ControllerMapper.getInstance();
    private final RegisterController registerController = (RegisterController) controllerMapper.resolve("/register");

    @DisplayName("GET 요청의 동작을 확인한다.")
    @Test
    void doGet() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = registerController.doGet(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.OK);
        assertContainsBodyString(httpResponseMessage, "회원 가입");
    }

    @DisplayName("POST 요청의 동작을 확인한다.")
    @Test
    void doPost() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 52",
                "",
                "account=ggyool&password=123&email=ggyool%40naver.com");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = registerController.doPost(httpRequestMessage);

        // then
        assertSavedUser();
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/index.html");
    }

    private void assertSavedUser() {
        User user = InMemoryUserRepository.findByAccount("ggyool").get();
        assertThat(user.getId()).isNotZero();
        assertThat(user.getAccount()).isEqualTo("ggyool");
        assertThat(user.getPassword()).isEqualTo("123");
        assertThat(user.getEmail()).isEqualTo("ggyool@naver.com");
    }
}
