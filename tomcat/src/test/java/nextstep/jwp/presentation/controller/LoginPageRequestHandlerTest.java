package nextstep.jwp.presentation.controller;

import static nextstep.jwp.presentation.ResourceLocation.ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import customservlet.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginPageRequestHandlerTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private SessionManager sessionManager;
    private LoginPageRequestHandler loginPageRequestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.sessionManager = mock(SessionManager.class);
        this.loginPageRequestHandler = new LoginPageRequestHandler(sessionManager);
    }

    @Test
    void 로그인_페이지_요청시_유효한_JSESSION이_존재하면_index페이지로_리다이렉트한다() {
        // given
        given(sessionManager.isValid(any())).willReturn(true);

        // when
        final var viewName = loginPageRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(ROOT.getLocation()),
                () -> assertThat(viewName).isNull()
        );
    }

    @Test
    void 로그인_페이지_요청시_유효한_JSESSION이_없다면_로그인_ViewName을_반환한다() {
        // given, when
        final var viewName = loginPageRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertThat(viewName).isEqualTo("login");
    }
}
