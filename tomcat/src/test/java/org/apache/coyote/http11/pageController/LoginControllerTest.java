package org.apache.coyote.http11.pageController;

import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    private final LoginController loginController = new LoginController();

    @Test
    void loginPage() throws IOException {
        // when
        final String response = run("GET /login HTTP/1.1");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK ")
                .endsWith(readResource("static/login.html"));
    }

    @Test
    void loginPageWhenPasswordIsMissing() throws IOException {
        // when
        final String response = run("GET /login?account=gugu HTTP/1.1");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK ")
                .endsWith(readResource("static/login.html"));
    }

    @Test
    void redirectToIndexWhenLoginSucceeds() throws IOException {
        // when
        final String response = run("GET /login?account=gugu&password=password HTTP/1.1");

        // then
        assertThat(response).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void redirectToUnauthorizedWhenPasswordIsWrong() throws IOException {
        // when
        final String response = run("GET /login?account=gugu&password=invalid HTTP/1.1");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
    }

    @Test
    void redirectToUnauthorizedWhenAccountDoesNotExist() throws IOException {
        // when
        final String response = run("GET /login?account=unknown&password=password HTTP/1.1");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
    }

    @Test
    void redirectToUnauthorizedWhenValuesAreEmpty() throws IOException {
        // when
        final String response = run("GET /login?account=&password= HTTP/1.1");

        // then
        assertThat(response).isEqualTo(redirectResponse("/401.html"));
    }

    private String run(String requestLine) throws IOException {
        final HttpRequest request = HttpRequest.from(requestLine, HttpHeaders.empty(), HttpBody.empty());

        return new String(loginController.run(request).toBytes(), StandardCharsets.UTF_8);
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
