package nextstep.org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.DuplicateAccountRegisterException;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class RegisterControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        controller = new RegisterController();
    }

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void registerSuccess() throws Exception {
        // given
        final String requestString = RequestFixture.createLine(HttpMethod.POST, "/register",
                "account=accountName&password=password&email=gugu@naver.com");
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
    void registerFailure() throws IOException, URISyntaxException {
        final String requestString = RequestFixture.createLine(HttpMethod.POST, "/register",
                "account=gugu&password=wrongPassword");
        setRequestAndResponse(requestString);

        // when
        assertThatThrownBy(() -> controller.service(request, response))
                .isExactlyInstanceOf(DuplicateAccountRegisterException.class);
    }
}
