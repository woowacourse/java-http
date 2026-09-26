package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestInput;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void login_success() throws Exception {
        // given
        final HttpRequest request = createPostRequest(
                "account=gugu&password=password"
        );

        final HttpResponse response = new HttpResponse();
        final LoginController controller = new LoginController();

        // when
        controller.service(request, response);

        // then
        final User user = (User) request.getSession()
                .getAttribute("user");

        assertThat(user).isNotNull();
        assertThat(user.getAccount()).isEqualTo("gugu");

        assertThat(response.toResponse())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }

    @Test
    void login_failure() throws Exception {
        // given
        final HttpRequest request = createPostRequest(
                "account=gugu&password=wrong-password"
        );

        final HttpResponse response = new HttpResponse();
        final LoginController controller = new LoginController();

        // when
        controller.service(request, response);

        // then
        assertThat(
                request.getSession().getAttribute("user")
        ).isNull();

        assertThat(response.toResponse())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /401.html");
    }

    @Test
    void loggedInUser_getLogin_redirectIndex() throws Exception {
        // given
        final HttpRequest request = createGetRequest();

        request.getSession().setAttribute(
                "user",
                new User(
                        "gugu",
                        "password",
                        "gugu@test.com"
                )
        );

        final HttpResponse response = new HttpResponse();
        final LoginController controller = new LoginController();

        // when
        controller.service(request, response);

        // then
        assertThat(response.toResponse())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }

    private HttpRequest createPostRequest(final String body) {
        final String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        return createRequest(httpRequest);
    }

    private HttpRequest createGetRequest() {
        final String httpRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                "",
                ""
        );

        return createRequest(httpRequest);
    }

    private HttpRequest createRequest(final String httpRequest) {
        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        httpRequest.getBytes(StandardCharsets.UTF_8)
                );

        return new HttpRequest(
                new HttpRequestInput(inputStream)
        );
    }
}