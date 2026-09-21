package org.apache.coyote.http11.pageController;

import com.techcourse.db.InMemoryUserRepository;
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

class RegisterControllerTest {
    private static final HttpHeaders FORM_HEADERS =
            HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));

    private final SessionManager sessionManager = new SessionManager();
    private final RegisterController registerController = new RegisterController();

    @Test
    void registerPage() throws IOException {
        // given
        final HttpRequest request = request("GET /register HTTP/1.1", HttpHeaders.empty(), HttpBody.empty());

        // when
        final String response = toString(registerController.run(request));

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK ")
                .endsWith(readResource("static/register.html"));
    }

    @Test
    void registerAndRedirectToIndex() throws IOException {
        // when
        final String response = post("account=newbie&email=newbie%40woowahan.com&password=secret");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\nLocation: /index.html \r\n")
                .containsPattern("\r\nSet-Cookie: JSESSIONID=[0-9a-f-]{36} \r\n");
        assertThat(InMemoryUserRepository.findByAccount("newbie"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("secret")).isTrue());
    }

    @Test
    void registerPageWhenValueIsBlank() throws IOException {
        // when
        final String response = post("account=blank&email=&password=secret");

        // then
        assertBadRequestRegisterPage(response);
        assertThat(InMemoryUserRepository.findByAccount("blank")).isEmpty();
    }

    @Test
    void registerPageWhenValueIsWhitespace() throws IOException {
        // when
        final String response = post("account=whitespace&email=+++&password=secret");

        // then
        assertBadRequestRegisterPage(response);
        assertThat(InMemoryUserRepository.findByAccount("whitespace")).isEmpty();
    }

    @Test
    void registerPageWhenValueIsMissing() throws IOException {
        // when
        final String response = post("account=missing&password=secret");

        // then
        assertBadRequestRegisterPage(response);
        assertThat(InMemoryUserRepository.findByAccount("missing")).isEmpty();
    }

    @Test
    void registerPageWhenBodyIsEmpty() throws IOException {
        // when
        final String response = post("");

        // then
        assertBadRequestRegisterPage(response);
    }

    @Test
    void registerPageWhenAccountIsDuplicated() throws IOException {
        // when
        final String response = post("account=gugu&email=other%40woowahan.com&password=changed");

        // then
        assertConflictRegisterPage(response);
        assertThat(response).doesNotContain("Set-Cookie");
        final User gugu = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        assertThat(gugu.checkPassword("password")).isTrue();
    }

    private String post(String body) throws IOException {
        final HttpRequest request = request("POST /register HTTP/1.1", FORM_HEADERS, new HttpBody(body));

        return toString(registerController.run(request));
    }

    private void assertBadRequestRegisterPage(String response) throws IOException {
        assertRegisterPage(response, "400 Bad Request");
    }

    private void assertConflictRegisterPage(String response) throws IOException {
        assertRegisterPage(response, "409 Conflict");
    }

    private void assertRegisterPage(String response, String status) throws IOException {
        assertThat(response)
                .startsWith("HTTP/1.1 " + status + " ")
                .endsWith(readResource("static/register.html"));
    }

    private String toString(HttpResponse response) {
        return new String(response.toBytes(), StandardCharsets.UTF_8);
    }

    private String readResource(String resourceName) throws IOException {
        final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName);

        try (InputStream inputStream = Objects.requireNonNull(resourceStream)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void registeredUserIsLoggedIn() throws IOException {
        // when
        final String response = post("account=sessionuser&email=sessionuser%40woowahan.com&password=secret");

        // then
        final Matcher matcher = Pattern.compile("Set-Cookie: JSESSIONID=([0-9a-f-]{36})").matcher(response);
        assertThat(matcher.find()).isTrue();
        final Session session = sessionManager.findSession(matcher.group(1));
        assertThat(((User) session.getAttribute("user")).getAccount()).isEqualTo("sessionuser");
    }

    @Test
    void redirectToIndexWhenAlreadyLoggedIn() throws IOException {
        // given
        final Session session = new Session("register-already-logged-in");
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        sessionManager.add(session);
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: JSESSIONID=register-already-logged-in"));
        final HttpRequest request = request("GET /register HTTP/1.1", headers, HttpBody.empty());

        // when
        final String response = toString(registerController.run(request));

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\nLocation: /index.html \r\n")
                .doesNotContain("Set-Cookie");
    }
    private HttpRequest request(String requestLine, HttpHeaders headers, HttpBody body) {
        return HttpRequest.from(requestLine, headers, body, sessionManager);
    }

}
