package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginControllerTest {

    private final Controller controller = new LoginController(SessionManager.getInstance());

    @DisplayName("처리할 수 있는 요청인지 반환한다.")
    @ParameterizedTest
    @CsvSource({"GET /login, true", "POST /login, true", "GET /register, false"})
    void isSuitable(String methodAndPath, boolean expected) throws IOException {
        String request = String.join("\r\n",
                methodAndPath + " HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        boolean actual = controller.isSuitable(httpRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지를 반환한다.")
    @Test
    void login_Get() throws IOException {
        String request = String.join("\r\n",
                "GET /login HTTP/1.1\n" +
                        "Host: localhost:8080\n" +
                        "Connection: keep-alive\n" +
                        "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: 3905");
    }

    @DisplayName("이미 로그인한 사용자가 login 페이지에 접근하려고 하면 index.html으로 리다이렉트 시킨다.")
    @Test
    void login_Get_RedirectToIndexHtml() throws IOException {
        // when
        User user = new User("chris", "password", "email@google.com");
        Session session = new Session("user", user);
        SessionManager.getInstance()
                .add(session);
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getResponse())
                .contains("302 Found")
                .contains("Location: /index.html");
    }

    @DisplayName("로그인에 성공하면 세션을 반환한다.")
    @ParameterizedTest
    @CsvSource({"account=gugu&password=password, 30, /index.html, true",
            "account=gugu12&password=password, 32, /401.html, false",
            "account=gugu&password=password12, 32, /401.html, false"
    })
    void service_Post(String requestBody, String contentLength,
                      String expectedLocation, boolean expectedCookie) throws IOException {
        String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + contentLength,
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody);

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse httpResponse = HttpResponse.getNew();

        controller.service(httpRequest, httpResponse);

        boolean containsCookie = httpResponse.getResponse()
                .contains("Set-Cookie: JSESSIONID=");
        assertAll(
                () -> assertThat(httpResponse.getResponse())
                        .contains("302 Found")
                        .contains("Location: " + expectedLocation),
                () -> assertThat(containsCookie).isEqualTo(expectedCookie)
        );
    }
}
