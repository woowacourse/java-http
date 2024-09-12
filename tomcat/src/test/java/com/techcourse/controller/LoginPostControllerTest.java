package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.FormParameters;
import org.apache.coyote.http11.message.request.HttpBodyParser;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestBody;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginPostControllerTest {

    private UserService userService = new UserService();
    private LoginPostController controller = new LoginPostController(userService);

    @Test
    @DisplayName("account와 password가 모두 없는 경우 예외가 발생한다.")
    void loginWithoutAccountAndPasswordTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = new byte[0];

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.handle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("로그인에 필요한 데이터가 오지 않았습니다.");
    }

    @Test
    @DisplayName("account가 없는 경우 예외가 발생한다.")
    void loginWithoutAccountTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "password=1234".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.handle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account에 해당되는 form 파라미터가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("password가 없는 경우 예외가 발생한다.")
    void loginWithoutPasswordTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=abc".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.handle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password에 해당되는 form 파라미터가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("올바르지 않은 account와 password인 경우 예외가 발생한다.")
    void loginWithInvalidAccountAndPasswordTest() throws IOException {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=abc&password=abc".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when
        HttpResponse response = controller.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.LOCATION))
                        .contains("http://localhost:8080/401.html")
        );
    }

    @Test
    @DisplayName("세션이 없는 경우 로그인에 성공하고 Set-Cookie에 세션이 설정된다.")
    void loginWithoutSessionTest() throws IOException {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=gugu&password=password".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when
        HttpResponse response = controller.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.LOCATION))
                        .contains("http://localhost:8080/index.html"),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.SET_COOKIE).get())
                        .startsWith("JSESSIONID=")
        );
    }

    @Test
    @DisplayName("세션이 있는 경우 로그인에 성공한다.")
    void loginWithSessionTest() throws IOException {
        // given
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        session.setAttribute("user", InMemoryUserRepository.findByAccountAndPassword("gugu", "password"));
        SessionManager.getInstance().add(session);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        requestHeaders.setHeader(HttpHeaderName.COOKIE, "JSESSIONID=" + uuid);
        byte[] requestBody = "account=gugu&password=password".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/login"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when
        HttpResponse response = controller.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.LOCATION))
                        .contains("http://localhost:8080/index.html"),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.SET_COOKIE)).isEmpty()
        );
    }
}
