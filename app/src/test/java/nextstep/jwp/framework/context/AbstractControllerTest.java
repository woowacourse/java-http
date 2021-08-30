package nextstep.jwp.framework.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpVersion;
import nextstep.jwp.webserver.controller.WelcomePageController;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class AbstractControllerTest {

    @DisplayName("등록되지 않은 HTTP Method 일 경우 예외 발생")
    @Test
    void handleTest() {

        // given
        HttpRequest httpRequest = new HttpRequest.Builder().requestLine(HttpMethod.PUT, "/", HttpVersion.HTTP_1_1)
                                                           .build();

        // when
        ThrowableAssert.ThrowingCallable callable = () -> new WelcomePageController().handle(httpRequest);

        // then
        assertThatIllegalArgumentException().isThrownBy(callable);
    }
}
