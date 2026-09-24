package org.apache.coyote.http11.pageController;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    private static final String SET_SESSION_COOKIE =
            "\r\nSet-Cookie: JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12} \r\n";
    private static final HttpHeaders FORM_HEADERS =
            HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));

    private final SessionManager sessionManager = new SessionManager();
    private final LoginController loginController = new LoginController();

    @Test
    void loginPage() throws IOException {
        // when
        final String response = get("GET /login HTTP/1.1");

        // then
        assertLoginPage(response);
    }

    @Test
    void queryStringDoesNotLogin() throws IOException {
        // when
        final String response = get("GET /login?account=gugu&password=password HTTP/1.1");

        // then
        assertLoginPage(response);
    }

    @Test
    void redirectToIndexWhenLoginSucceeds() throws IOException {
        // when
        final String response = post("account=gugu&password=password");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\nLocation: /index.html \r\n")
                .containsPattern(SET_SESSION_COOKIE);
    }

    @Test
    void newSessionCookieEvenIfRequestHasJSessionId() throws IOException {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of(
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=before-login"
        ));
        final HttpRequest request = request(
                "POST /login HTTP/1.1", headers, new HttpBody("account=gugu&password=password"));

        // when
        final String response = toString(service(request));

        // then
        assertThat(response)
                .containsPattern(SET_SESSION_COOKIE)
                .doesNotContain("before-login");
    }

    @Test
    void redirectToUnauthorizedWhenPasswordIsWrong() throws IOException {
        // when
        final String response = post("account=gugu&password=invalid");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
        assertThat(response).doesNotContain("Set-Cookie");
    }

    @Test
    void redirectToUnauthorizedWhenAccountDoesNotExist() throws IOException {
        // when
        final String response = post("account=unknown&password=password");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
    }

    @Test
    void loginPageWhenValuesAreEmpty() throws IOException {
        // when
        final String response = post("account=&password=");

        // then
        assertBadRequestLoginPage(response);
    }

    @Test
    void loginPageWhenValuesAreWhitespace() throws IOException {
        // when
        final String response = post("account=+++&password=+++");

        // then
        assertBadRequestLoginPage(response);
    }

    @Test
    void loginPageWhenPasswordIsMissing() throws IOException {
        // when
        final String response = post("account=gugu");

        // then
        assertBadRequestLoginPage(response);
    }

    @Test
    void loginPageWhenBodyIsEmpty() throws IOException {
        // when
        final String response = post("");

        // then
        assertBadRequestLoginPage(response);
    }

    private String get(String requestLine) throws IOException {
        final HttpRequest request = request(requestLine, HttpHeaders.empty(), HttpBody.empty());

        return toString(service(request));
    }

    private String post(String body) throws IOException {
        final HttpRequest request = request("POST /login HTTP/1.1", FORM_HEADERS, new HttpBody(body));

        return toString(service(request));
    }

    private void assertLoginPage(String response) throws IOException {
        assertLoginPage(response, "200 OK");
    }

    private void assertBadRequestLoginPage(String response) throws IOException {
        assertLoginPage(response, "400 Bad Request");
    }

    private void assertLoginPage(String response, String status) throws IOException {
        assertThat(response)
                .startsWith("HTTP/1.1 " + status + " ")
                .endsWith(readResource("static/login.html"));
    }

    private String toString(HttpResponse response) {
        return new String(response.toBytes(), StandardCharsets.UTF_8);
    }

    private HttpResponse service(HttpRequest request) throws IOException {
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);
        return response;
    }

    private String redirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
    }

    private String readResource(String resourceName) throws IOException {
        final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName);

        try (InputStream inputStream = Objects.requireNonNull(resourceStream)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void userIsStoredInSessionWhenLoginSucceeds() throws IOException {
        // when
        final String response = post("account=gugu&password=password");

        // then
        final Session session = sessionManager.findSession(sessionIdOf(response));
        assertThat(session).isNotNull();
        assertThat(((User) session.getAttribute("user")).getAccount()).isEqualTo("gugu");
    }

    @Test
    void redirectToIndexWhenAlreadyLoggedIn() throws IOException {
        // given
        final Session session = new Session("login-already-logged-in");
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        sessionManager.add(session);

        // when
        final String response = getWithCookie("JSESSIONID=login-already-logged-in");

        // then
        assertThat(response).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void loginPageWhenSessionHasNoUser() throws IOException {
        // given
        sessionManager.add(new Session("login-session-without-user"));

        // when
        final String response = getWithCookie("JSESSIONID=login-session-without-user");

        // then
        assertLoginPage(response);
    }

    @Test
    void loginPageWhenSessionIsUnknown() throws IOException {
        // when
        final String response = getWithCookie("JSESSIONID=login-unknown-session");

        // then
        assertLoginPage(response);
    }

    private String getWithCookie(String cookie) throws IOException {
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: " + cookie));
        final HttpRequest request = request("GET /login HTTP/1.1", headers, HttpBody.empty());

        return toString(service(request));
    }

    private String sessionIdOf(String response) {
        final Matcher matcher = Pattern.compile("Set-Cookie: JSESSIONID=([0-9a-f-]{36})").matcher(response);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
    private HttpRequest request(String requestLine, HttpHeaders headers, HttpBody body) {
        return HttpRequest.from(requestLine, headers, body, sessionManager);
    }

}
