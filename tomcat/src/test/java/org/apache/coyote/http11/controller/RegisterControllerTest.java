package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.exception.ClientRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestBuilder;

class RegisterControllerTest extends AbstractControllerTest {

    private final Pattern SESSION_PATTERN = Pattern.compile("(?<=JSESSIONID=).+(?= )");

    @DisplayName("GET /register 요청 시 회원가입 페이지가 나온다.")
    @Test
    void getRegisterPage() throws IOException {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/register")
                .build();
        Controller controller = new RegisterController();
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        controller.service(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }

    @DisplayName("POST /register 요청 시 가입이 성공하며, 가입한 회원의 세션이 저장된다.")
    @Test
    void signUpSuccess() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("POST", "/register")
                .setRequestBody("account=poke&email=email@poke.com&password=password2")
                .build();
        Controller controller = new RegisterController();
        String expected = "poke";

        // when
        controller.service(request, response);
        String httpResponse = outputStream.toString();
        String sessionId = getSessionId(httpResponse);
        Session session = SessionManager.getInstance().findSessionById(sessionId);
        User user = (User) session.findValue("user");
        String actual = user.getAccount();

        // then
        assertThat(actual).contains(expected);
    }


    @DisplayName("POST /register 요청 시 이미 가입한 계정일 경우 예외가 발생한다.")
    @Test
    void signUpFailWithDuplicateMember() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("POST", "/register")
                .setRequestBody("account=gugu&email=email@gugu.com&password=password")
                .build();
        Controller controller = new RegisterController();

        // when
        assertThatThrownBy(() -> controller.service(request, response))
                // then
                .isInstanceOf(ClientRequestException.class);
    }

    @DisplayName("POST /register 요청 시 필수 값이 누락될 경우 예외가 발생한다.")
    @Test
    void signUpFailWithEmptyRequiredParameter() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("POST", "/register")
                .setRequestBody("account=&email=email@gugu.com&password=")
                .build();
        Controller controller = new RegisterController();

        // when
        assertThatThrownBy(() -> controller.service(request, response))
                // then
                .isInstanceOf(ClientRequestException.class);
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