package org.apache.coyote.http11.pageController;

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

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    private static final HttpHeaders FORM_HEADERS =
            HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));

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
        assertThat(response).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void redirectToUnauthorizedWhenPasswordIsWrong() throws IOException {
        // when
        final String response = post("account=gugu&password=invalid");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
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
        assertLoginPage(response);
    }

    @Test
    void loginPageWhenValuesAreWhitespace() throws IOException {
        // when
        final String response = post("account=+++&password=+++");

        // then
        assertLoginPage(response);
    }

    @Test
    void loginPageWhenPasswordIsMissing() throws IOException {
        // when
        final String response = post("account=gugu");

        // then
        assertLoginPage(response);
    }

    @Test
    void loginPageWhenBodyIsEmpty() throws IOException {
        // when
        final String response = post("");

        // then
        assertLoginPage(response);
    }

    private String get(String requestLine) throws IOException {
        final HttpRequest request = HttpRequest.from(requestLine, HttpHeaders.empty(), HttpBody.empty());

        return toString(loginController.run(request));
    }

    private String post(String body) throws IOException {
        final HttpRequest request = HttpRequest.from("POST /login HTTP/1.1", FORM_HEADERS, new HttpBody(body));

        return toString(loginController.run(request));
    }

    private void assertLoginPage(String response) throws IOException {
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK ")
                .endsWith(readResource("static/login.html"));
    }

    private String toString(HttpResponse response) {
        return new String(response.toBytes(), StandardCharsets.UTF_8);
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
}
