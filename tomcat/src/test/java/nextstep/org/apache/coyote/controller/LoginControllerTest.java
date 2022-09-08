package nextstep.org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.Controller;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.StubSocket;

class LoginControllerTest {

    private StubSocket stubSocket;
    private Controller loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
    }

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void isRunnable() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/login", "");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());

        // when
        final boolean actual = loginController.isRunnable(request);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void loginSuccess() throws IOException, URISyntaxException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.POST, "/lgoin", "account=gugu&password=password");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());
        final Response response = Response.of(stubSocket.getOutputStream());

        // when
        loginController.run(request, response);

        // then
        assertAll(
                () -> assertThat(stubSocket.output()).contains("FOUND"),
                () -> assertThat(stubSocket.output()).contains("/index.html")
        );
    }

    @Test
    void loginFailure() throws IOException, URISyntaxException {
        final String requestString = RequestFixture.create(HttpMethod.POST, "/lgoin", "account=gugu&password=wrongPassword");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());
        final Response response = Response.of(stubSocket.getOutputStream());

        // when
        loginController.run(request, response);

        // then
        assertAll(
                () -> assertThat(stubSocket.output()).contains("FOUND"),
                () -> assertThat(stubSocket.output()).contains("/401.html")
        );
    }
}
