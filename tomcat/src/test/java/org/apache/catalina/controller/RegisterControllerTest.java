package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.support.FileFinder;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RegisterControllerTest {

    @Test
    @DisplayName("회원가입 성공시 index 페이지로 리다이랙트한다.")
    void success_register() throws Exception {
        // given
        final Map<String, String> registerInfo = new HashMap<>();
        registerInfo.put("account", "ako");
        registerInfo.put("email", "ako@ako.com");
        registerInfo.put("password", "ako");

        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.POST,
            "/login",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(registerInfo));

        final HttpResponse httpResponse = new HttpResponse();
        final RegisterController registerController = new RegisterController();

        // when
        registerController.doPost(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: /index.html \r\n" +
            "\r\n" +
            "null";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
        " '', ako@ako.com, password",
        "ako, '' , password,",
        "ako, ako@ako.com, '' "})
    @DisplayName("회원가입시 올바르지 값(빈값)을 넣은 경우 다시 회원가입 페이지로 리다이랙트한다.")
    void invalid_input_register_info(
        final String account,
        final String email,
        final String password
    ) throws Exception {
        // given
        final Map<String, String> registerInfo = new HashMap<>();
        registerInfo.put("account", account);
        registerInfo.put("email", email);
        registerInfo.put("password", password);

        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.POST,
            "/login",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(registerInfo));

        final HttpResponse httpResponse = new HttpResponse();
        final RegisterController registerController = new RegisterController();

        // when
        registerController.doPost(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 400 Bad Request \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 87 \r\n" +
            "\r\n" +
            "입력한 아이디, 비밀번호, 이메일에는 공백이 들어오면 안됩니다.";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인한 상태가 아닌 경우 회원가입 화면으로 이동한다.")
    void load_register_page() throws Exception {
        // given
        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            "/register",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final RegisterController registerController = new RegisterController();

        // when
        registerController.doGet(httpRequest, httpResponse);

        // then
        final String body = FileFinder.find("/register.html");
        final String bodyLength = String.valueOf(body.getBytes().length);
        final String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + bodyLength + " \r\n" +
            "\r\n" +
            body;

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인한 상태인 경우 index 화면으로 리다이랙트한다.")
    void redirect_index_page() throws Exception {
        // given
        SessionManager.add(new Session("member"));

        final Map<String, String> header = new HashMap<>();
        header.put("Cookie", "JSESSIONID=member");

        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            "/register",
            "HTTP/1.1",
            new HttpHeader(header),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final RegisterController registerController = new RegisterController();

        // when
        registerController.doGet(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: /index.html \r\n" +
            "\r\n" +
            "null";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }
}
