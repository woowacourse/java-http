package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.line.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.apache.coyote.http11.response.line.ResponseStatus.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nextstep.jwp.service.AuthService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginControllerTest {

    @Mock
    private HttpRequest mockHttpRequest;

    @Mock
    private HttpResponse mockHttpResponse;

    @Mock
    private AuthService authService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class uri가_일치하는_POST_요청일_때 {

        @Test
        void body_값으로_로그인하면_쿠키에_세션을_넣고_FOUND로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(POST, "/login"))
                    .thenReturn(true);
            when(authService.login(any(), any()))
                    .thenReturn("sessionId");

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(authService, times(1)).login(any(), any());
            InOrder inOrder = Mockito.inOrder(mockHttpResponse);
            inOrder.verify(mockHttpResponse, times(1)).setResponseRedirect(FOUND, "/index.html");
            inOrder.verify(mockHttpResponse, times(1)).setResponseHeader(SET_COOKIE, "JSESSIONID=sessionId");
        }

        @Test
        void body_값으로_로그인_중_예외가_발생하면_Unauthorized로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(POST, "/login"))
                    .thenReturn(true);
            when(authService.login(any(), any()))
                    .thenThrow(IllegalArgumentException.class);

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(authService, times(1)).login(any(), any());
            verify(mockHttpResponse, times(1)).setResponseResource(UNAUTHORIZED, "/401.html");
        }
    }

    @Nested
    class uri가_일치하는_GET_요청일_때 {

        @Test
        void query_String_으로_로그인되면_쿠키에_세션을_넣고_FOUND로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET, "/login"))
                    .thenReturn(true);
            when(mockHttpRequest.hasQueryString())
                    .thenReturn(true);
            when(authService.login(any(), any()))
                    .thenReturn("sessionId");

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(authService, times(1)).login(any(), any());
            InOrder inOrder = Mockito.inOrder(mockHttpResponse);
            inOrder.verify(mockHttpResponse, times(1)).setResponseRedirect(FOUND, "/index.html");
            inOrder.verify(mockHttpResponse, times(1)).setResponseHeader(SET_COOKIE, "JSESSIONID=sessionId");
        }

        @Test
        void query_String_으로_로그인하다가_예외가_발생하면_Unauthorized로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET, "/login"))
                    .thenReturn(true);
            when(mockHttpRequest.hasQueryString())
                    .thenReturn(true);
            when(authService.login(any(), any()))
                    .thenThrow(IllegalArgumentException.class);

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(authService, times(1)).login(any(), any());
            verify(mockHttpResponse, times(1)).setResponseResource(UNAUTHORIZED, "/401.html");
        }

        @Test
        void session으로_기존_로그인을_확인하고_FOUND로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET, "/login"))
                    .thenReturn(true);
            when(mockHttpRequest.hasSessionId())
                    .thenReturn(true);
            when(authService.isLoggedIn(any()))
                    .thenReturn(true);

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(mockHttpResponse, times(1)).setResponseRedirect(FOUND, "/index.html");
        }

        @Test
        void OK로_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET, "/login"))
                    .thenReturn(true);

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(mockHttpResponse, times(1)).setResponseResource(OK, "/login.html");
        }
    }
}
