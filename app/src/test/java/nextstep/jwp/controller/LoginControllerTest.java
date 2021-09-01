package nextstep.jwp.controller;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.authentication.HttpSession;
import nextstep.jwp.http.authentication.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.request.HttpRequestHeader;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static nextstep.jwp.controller.StaticResourceControllerTest.staticResourceRequest;
import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    protected static final HttpRequest loginRequest = new HttpRequest(
            new HttpRequestHeader(List.of("POST /login HTTP/1.1 ")),
            new HttpRequestBody("account=gugu&password=password")
    );

    private final AbstractController loginController = new LoginController(new UserService());

    @DisplayName("컨트롤러가 해당 요청을 처리할 수 있으면 true, 아니면 false")
    @Test
    void canHandle() {
        assertThat(loginController.canHandle(loginRequest)).isTrue();
        assertThat(loginController.canHandle(staticResourceRequest)).isFalse();
    }

    @DisplayName("cookie 없이 get요청이 들어오면, login.html페이지를 반환 한다")
    @Test
    void doGet_without_cookie() {
        final HttpRequest loginGetRequestWithoutCookie = new HttpRequest(
                new HttpRequestHeader(List.of("GET /login HTTP/1.1 ")),
                null);

        final HttpResponse actual = loginController.doGet(loginGetRequestWithoutCookie);

        final String url = "/login.html";
        final String responseBody = loginController.readFile(url);
        final HttpResponse expected = new HttpResponse(
                loginGetRequestWithoutCookie.getProtocol(),
                HttpStatus.OK,
                ContentType.HTML,
                responseBody.getBytes().length,
                responseBody);

        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }

    @DisplayName("cookie와 함께 get요청이 들어오면, login.html페이지를 반환 한다")
    @Test
    void doGet_with_cookie() {
        final String uuid = UUID.randomUUID().toString();
        final HttpSession httpSession = new HttpSession(uuid);
        HttpSessions.addSession(uuid, httpSession);

        final HttpRequest loginGetRequestWithCookie = new HttpRequest(
                new HttpRequestHeader(List.of("GET /login HTTP/1.1 ", "Cookie: JSESSIONID=" + uuid)),
                null);

        final HttpResponse actual = loginController.doGet(loginGetRequestWithCookie);

        final String redirectUrl = "/index.html";
        final HttpResponse expected = new HttpResponse(
                loginGetRequestWithCookie.getProtocol(),
                HttpStatus.FOUND,
                redirectUrl
        );

        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }

    @DisplayName("get요청을 성공적으로 핸들링 하면 index.html페이지로 리다이렉트 한다")
    @Test
    void doGet_fail() {
        final HttpResponse actual = loginController.doPost(loginRequest);

        final String redirectUrl = "/index.html";
        final HttpResponse expected = new HttpResponse(
                loginRequest.getProtocol(),
                HttpStatus.FOUND,
                redirectUrl
        );

        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }

    @DisplayName("post요청을 성공적으로 핸들링 하면 index.html페이지를 반환한다")
    @Test
    void doPost_success() {
        final HttpResponse actual = loginController.doPost(loginRequest);

        final String redirectUrl = "/index.html";
        final HttpResponse expected = new HttpResponse(
                loginRequest.getProtocol(),
                HttpStatus.FOUND,
                redirectUrl
        );

        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }

    @DisplayName("post요청을 핸들링에 실패하면 401 페이지를 반환한다")
    @Test
    void doPost_fail() {
        final HttpRequest wrongLoginRequest = new HttpRequest(
                new HttpRequestHeader(List.of("POST /login HTTP/1.1 ")),
                new HttpRequestBody("account=wow&password=password")
        );

        final HttpResponse actual = loginController.doPost(wrongLoginRequest);

        final String unauthorizedUrl = "/401.html";
        final String responseBody = loginController.readFile(unauthorizedUrl);

        final HttpResponse expected = new HttpResponse(
                wrongLoginRequest.getProtocol(),
                HttpStatus.findHttpStatusByUrl(unauthorizedUrl),
                ContentType.findByUrl(unauthorizedUrl),
                responseBody.getBytes().length,
                responseBody);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
