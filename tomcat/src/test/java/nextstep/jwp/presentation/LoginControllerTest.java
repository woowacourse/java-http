package nextstep.jwp.presentation;

import coyote.http.RequestFixture;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoginControllerTest {

    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        httpResponse = new HttpResponse();
    }

    @Test
    void processGet() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.GET_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        LoginController loginController = new LoginController();

        //when
        loginController.process(httpRequest, httpResponse);

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 3797")
        );
    }

    @Test
    void processGetIfAlreadyLoginGetMapping() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.GET_LOGIN_REDIRECT_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        LoginController loginController = new LoginController();

        SessionManager.add("656cef62-e3c4-40bc-a8df-94732920ed46", new User("test", "test", "test"));

        //when
        loginController.process(httpRequest, httpResponse);

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Location: /index.html"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

    @Test
    void processPostIfUserExist() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.POST_SUCCESS_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        LoginController postLoginController = new LoginController();

        //when
        postLoginController.process(httpRequest, httpResponse);

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Location: /index.html"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

    @Test
    void processPostIfNonExistUser() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.POST_FAIL_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        LoginController postLoginController = new LoginController();

        //when
        postLoginController.process(httpRequest, httpResponse);

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Location: /401.html"),
                () -> assertThat(response).contains("Content-Length: 2426")
        );
    }
}
