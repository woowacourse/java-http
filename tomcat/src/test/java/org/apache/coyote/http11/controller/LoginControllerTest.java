package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.exception.ClientRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import support.HttpRequestBuilder;

class LoginControllerTest extends AbstractControllerTest {

    private final Pattern SESSION_PATTERN = Pattern.compile("(?<=JSESSIONID=).+(?= )");

    @DisplayName("GET /login 요청 시, login.html을 응답한다.")
    @Test
    void getLoginWithNoParameter() throws IOException {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login")
                .build();
        LoginController controller = new LoginController();
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        controller.doGet(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }

    @DisplayName("GET /login 요청 시 유효한 파라미터가 있을 경우, index.html로 리디렉트 한다.")
    @Test
    void getLoginWithSuccessfulParameter() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login?account=gugu&password=password")
                .build();
        LoginController controller = new LoginController();
        List<String> expected = List.of("302 Found", "Location: index.html");

        // when
        controller.doGet(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }

    @DisplayName("로그인 성공 후 파라미터 없이 재 로그인이 가능하다.")
    @Test
    void getSession() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login?account=gugu&password=password")
                .build();
        LoginController controller = new LoginController();
        String expected = "gugu";

        // when
        controller.doGet(request, response);
        String httpResponse = outputStream.toString();
        String sessionId = getSessionId(httpResponse);
        Session session = SessionManager.getInstance().findSessionById(sessionId);
        User user = (User) session.findValue("user");
        String actual = user.getAccount();

        // then
        assertThat(actual).contains(expected);
    }

    @DisplayName("로그인이 성공한 후 GET /login 시 index.html로 리디렉트 한다.")
    @TestFactory
    Stream<DynamicTest> redirectIndexAfterLoginSuccess() {
        return Stream.of(
                dynamicTest("로그인에 성공한다.", () -> {
                    // given
                    HttpRequest request = HttpRequestBuilder.builder()
                            .setRequest("GET", "/login?account=gugu&password=password")
                            .build();
                    LoginController controller = new LoginController();
                    List<String> expected = List.of("302 Found", "Location: index.html", "Set-Cookie: JSESSIONID=");

                    // when
                    controller.doGet(request, response);
                    String actual = outputStream.toString();

                    // then
                    assertThat(actual).contains(expected);
                }),
                dynamicTest("GET / 시 index.html로 리디렉트 된다.", () -> {
                    HttpRequest request = HttpRequestBuilder.builder()
                            .setRequest("GET", "/login")
                            .build();
                    LoginController controller = new LoginController();
                    List<String> expected = List.of("302 Found", "Location: index.html");

                    // when
                    controller.doGet(request, response);
                    String actual = outputStream.toString();

                    // then
                    assertThat(actual).contains(expected);
                })
        );
    }

    @DisplayName("로그인이 실패하면 예외가 발생한다.")
    @Test
    void loginFail() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login?account=gugu&password=invalid_password")
                .build();
        LoginController controller = new LoginController();

        // when
        assertThatThrownBy(() -> controller.doGet(request, response))
                .isInstanceOf(ClientRequestException.class);
    }

    @DisplayName("계정이 존재하지 않으면 예외가 발생한다.")
    @Test
    void loginFailAccountNotFound() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login?account=poke&password=invalid_password")
                .build();
        LoginController controller = new LoginController();

        // when
        assertThatThrownBy(() -> controller.doGet(request, response))
                .isInstanceOf(ClientRequestException.class);
    }

    @DisplayName("잘못된 세션일 경우. login.html로 이동한다.")
    @Test
    void loginFailInvalidSession() throws IOException {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login")
                .addRequestHeader("Cookie:", "JSESSIONID=invalid_session")
                .build();
        LoginController controller = new LoginController();
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        controller.doGet(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }

    private String getSessionId(String httpResponse) {
        Matcher matcher = SESSION_PATTERN.matcher(httpResponse);
        String sessionId = "no-session-id";
        while (matcher.find()) {
            sessionId = matcher.group();
        }
        return sessionId;
    }
}