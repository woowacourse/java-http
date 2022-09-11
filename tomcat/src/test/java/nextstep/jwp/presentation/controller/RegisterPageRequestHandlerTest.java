package nextstep.jwp.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RegisterPageRequestHandlerTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private RegisterPageRequestHandler registerPageRequestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = Mockito.mock(HttpRequest.class);
        this.httpResponse = Mockito.mock(HttpResponse.class);
        this.registerPageRequestHandler = new RegisterPageRequestHandler();
    }

    @Test
    void 회원가입_ViewName을_반환한다() {
        // given, when
        final String viewName = registerPageRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertAll(
                () -> Mockito.verify(httpResponse).setStatusCode(HttpStatus.OK),
                () -> assertThat(viewName).isEqualTo("register")
        );
    }
}
