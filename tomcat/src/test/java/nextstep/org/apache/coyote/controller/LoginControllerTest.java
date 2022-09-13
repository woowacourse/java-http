package nextstep.org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import nextstep.jwp.controller.LoginController;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class LoginControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        controller = new LoginController(SessionManager.getInstance());
    }

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void loginSuccess() throws Exception {
        // given
        final String requestString = RequestFixture.createLine(HttpMethod.POST, "/lgoin", "account=gugu&password=password");
        setRequestAndResponse(requestString);

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(stubSocket.output()).contains("FOUND"),
                () -> assertThat(stubSocket.output()).contains("/index.html")
        );
    }

    @Test
    void loginFailure() throws Exception {
        final String requestString = RequestFixture.createLine(HttpMethod.POST, "/lgoin", "account=gugu&password=wrongPassword");
        setRequestAndResponse(requestString);

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(stubSocket.output()).contains("FOUND"),
                () -> assertThat(stubSocket.output()).contains("/401.html")
        );
    }
}
