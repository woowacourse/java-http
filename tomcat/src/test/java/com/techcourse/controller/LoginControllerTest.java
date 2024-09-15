package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.HttpRequestFixture;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.Header;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @DisplayName("로그인하지 않은 유저의 경우 login.html을 응답한다.")
    @Test
    void doGet() throws IOException {
        HttpResponse response = new HttpResponse();
        loginController.doGet(HttpRequestFixture.getHttpGetRequest("/login.html"), response);

        File file = new File(getClass().getClassLoader().getResource("static/login.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("로그인한 유저의 경우 index.html을 응답한다.")
    @Test
    void doGetWithLoginUser() throws IOException {
        HttpResponse response = new HttpResponse();
        HttpRequest httpGetRequest = HttpRequestFixture.getHttpGetRequestWithLoginUser("/login.html");
        Session session = new Session("1");
        session.setAttribute("user", new User("ayeonii", "1234", "tenny@wooteco.com"));
        sessionManager.add(session);

        loginController.doGet(httpGetRequest, response);

        File file = new File(getClass().getClassLoader().getResource("static/index.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("FOUND"),
                () -> assertThat(response.getHeaders().get(Header.LOCATION)).isEqualTo("/index.html"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("로그인에 성공하면 index.html로 리다이렉트하며 Cookie에 session 정보를 전달한다.")
    @Test
    void doPost() throws IOException {
        HttpResponse response = new HttpResponse();
        loginController.doPost(HttpRequestFixture.getHttpPostRequest("/login.html", "account=gugu&password=password"), response);

        File file = new File(getClass().getClassLoader().getResource("static/index.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("FOUND"),
                () -> assertThat(response.getHeaders().get(Header.LOCATION)).isEqualTo("/index.html"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getHeaders().get(Header.SET_COOKIE)).isNotNull(),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("없는 계정이라면 401.html로 리다이렉트한다.")
    @Test
    void doPostWithAbsentAccount() throws IOException {
        HttpResponse response = new HttpResponse();
        loginController.doPost(HttpRequestFixture.getHttpPostRequest("/login.html", "account=tenny&password=password"), response);

        File file = new File(getClass().getClassLoader().getResource("static/401.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("UNAUTHORIZED"),
                () -> assertThat(response.getHeaders().get(Header.LOCATION)).isEqualTo("/401.html"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("비밀번호가 다르면 401.html로 리다이렉트한다.")
    @Test
    void doPostWithWrongPassword() throws IOException {
        HttpResponse response = new HttpResponse();
        loginController.doPost(HttpRequestFixture.getHttpPostRequest("/login.html", "account=gugu&password=wrong"), response);

        File file = new File(getClass().getClassLoader().getResource("static/401.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("UNAUTHORIZED"),
                () -> assertThat(response.getHeaders().get(Header.LOCATION)).isEqualTo("/401.html"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }
}
