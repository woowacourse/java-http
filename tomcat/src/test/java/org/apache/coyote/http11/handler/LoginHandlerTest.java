package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {
    private LoginHandler loginHandler = new LoginHandler();

    @Mock
    private HttpRequest request;

    @Test
    void 로그인_요청을_처리할_수_있다() {
        // given
        when(request.getRequestPath()).thenReturn("/login");

        // when
        boolean canHandle = loginHandler.canHandle(request);

        // then
        assertThat(canHandle).isTrue();
    }


    @Test
    void 로그인_요청을_처리한다() throws IOException {
        // given
        HttpResponse response = loginHandler.handle(request);

        // when & then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getHeaders().get("Content-Type")).contains("text/html;charset=utf-8"),
                () -> assertThat(response.getBody().toText()).contains("Login Page")
        );
    }
}
