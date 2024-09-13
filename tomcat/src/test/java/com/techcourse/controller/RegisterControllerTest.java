package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.service.UserService;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
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

class RegisterControllerTest {

    private final UserService service = new UserService();
    private final RegisterController controller = new RegisterController(service);

    @Test
    @DisplayName("회원가입 페이지를 반환한다.")
    void getRegisterPageTest() throws Exception {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        HttpRequest request =
                new HttpRequest(HttpMethod.GET, new HttpUrl("/register"), requestHeaders, new HttpRequestBody());

        // when
        HttpResponse response = new HttpResponse();
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE))
                        .contains("text/html;charset=utf-8"),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.CONTENT_LENGTH))
                        .contains(String.valueOf(body.length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @Test
    @DisplayName("account와 password와 email 모두가 없는 경우 예외가 발생한다.")
    void registerWithoutAccountAndPasswordAndEmailTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = new byte[0];

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/register"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원가입에 필요한 데이터가 오지 않았습니다.");
    }

    @Test
    @DisplayName("account가 없는 경우 예외가 발생한다.")
    void registerWithoutAccountTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "password=1234&email=abc@abc.com".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/register"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account에 해당되는 form 파라미터가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("password가 없는 경우 예외가 발생한다.")
    void registerWithoutPasswordTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=abc&email=abc@abc.com".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/register"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password에 해당되는 form 파라미터가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("email이 없는 경우 예외가 발생한다.")
    void registerWithoutEmailTest() {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=abc&password=1234".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/register"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when & then
        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email에 해당되는 form 파라미터가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원가입에 성공하고 Set-Cookie에 세션이 설정된다.")
    void registerWithoutSessionTest() throws Exception {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        byte[] requestBody = "account=abc&password=1234&email=abc@abc.com".getBytes();

        FormParameters formParameters = HttpBodyParser.parseToFormParameters(requestBody, requestHeaders);

        HttpRequest request =
                new HttpRequest(HttpMethod.POST, new HttpUrl("/register"), requestHeaders,
                        new HttpRequestBody(requestBody, formParameters));

        // when
        HttpResponse response = new HttpResponse();
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.LOCATION))
                        .contains("http://localhost:8080/index.html"),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.SET_COOKIE).get())
                        .startsWith("JSESSIONID=")
        );
    }
}
