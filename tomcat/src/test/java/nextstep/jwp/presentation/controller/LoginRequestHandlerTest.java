package nextstep.jwp.presentation.controller;

import static nextstep.jwp.presentation.ResourceLocation.ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import customservlet.SessionManager;
import nextstep.jwp.application.MemberService;
import nextstep.jwp.exception.AuthenticationException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginRequestHandlerTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private Session session;
    private SessionManager sessionManager;
    private MemberService memberService;
    private LoginRequestHandler loginRequestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.session = mock(Session.class);
        this.sessionManager = mock(SessionManager.class);
        this.memberService = mock(MemberService.class);
        this.loginRequestHandler = new LoginRequestHandler(memberService, sessionManager);
    }

    @Test
    void 로그인에_성공하면_index_페이지로_리다이렉트한다() {
        // given
        given(httpRequest.getRequestBody()).willReturn("mock=mock");
        given(httpRequest.getSession(true)).willReturn(session);
        given(session.getId()).willReturn("");

        // when
        final String viewName = loginRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).addCookie(any()),
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(ROOT.getLocation()),
                () -> assertThat(viewName).isNull()
        );
    }

    @Test
    void 로그인에_실패하면_예외가_발생한다() {
        // given
        given(httpRequest.getRequestBody()).willReturn("mock=mock");
        willThrow(new AuthenticationException())
                .given(memberService)
                .login(any());

        // when, then
        assertThatThrownBy(() -> loginRequestHandler.handle(httpRequest, httpResponse))
                .isInstanceOf(AuthenticationException.class);
    }
}
