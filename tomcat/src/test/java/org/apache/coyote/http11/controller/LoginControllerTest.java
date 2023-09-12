package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.controller.support.FileFinder;
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

class LoginControllerTest {

    @Test
    @DisplayName("로그인을 성공한다.")
    void success_login_post() throws Exception {
        // given
        final Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("account", "gugu");
        loginInfo.put("password", "password");

        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.POST,
            "/login",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(loginInfo));

        final HttpResponse httpResponse = new HttpResponse();
        final LoginControllerTestImpl loginControllerTest = new LoginControllerTestImpl();

        // when
        loginControllerTest.doPost(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: /index.html \r\n" +
            "Set-Cookie: JSESSIONID=success \r\n" +
            "\r\n" +
            "null";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"ako,password", "gugu,password1", "ako,password1"})
    @DisplayName("로그인을 실패한다.")
    void fail_login_post(final String account, final String password) throws Exception {
        // given
        final Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("account", account);
        loginInfo.put("password", password);

        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.POST,
            "/login",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(loginInfo));

        final HttpResponse httpResponse = new HttpResponse();
        final LoginControllerTestImpl loginControllerTest = new LoginControllerTestImpl();

        // when
        loginControllerTest.doPost(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: /401.html \r\n" +
            "\r\n" +
            "null";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인한 상태가 아닌 경우 로그인 화면으로 이동한다.")
    void load_login_page() throws Exception {
        // given
        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            "/login",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final LoginController loginController = new LoginController();

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        final String body = FileFinder.find("/login.html");
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
            "/login",
            "HTTP/1.1",
            new HttpHeader(header),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final LoginController loginController = new LoginController();

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: /index.html \r\n" +
            "\r\n" +
            "null";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }
}
