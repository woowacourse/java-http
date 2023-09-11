package nextstep.jwp.controller;

import static common.ResponseStatus.FOUND;
import static common.ResponseStatus.OK;
import static org.apache.coyote.request.line.HttpMethod.GET;
import static org.apache.coyote.request.line.HttpMethod.POST;
import static org.apache.coyote.response.header.HttpHeader.SET_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.line.HttpMethod;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({"true, true", "false, false"})
    void 처리할_수_있는_요청인지_확인한다(boolean returnValue, boolean expected) {
        // given
        String uri = anyString();
        when(mockHttpRequest.consistsOf(uri))
                .thenReturn(returnValue);

        // when
        boolean actual = loginController.canProcess(mockHttpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    class POST_요청일_때 {

        @Test
        void 로그인하면_쿠키에_세션을_넣고_FOUND로_리다이렉트할_URL을_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(POST))
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
    }

    @Nested
    class GET_요청일_때 {

        @Test
        void query_String_으로_로그인하면_쿠키에_세션을_넣고_FOUND로_리다이렉트할_URL을_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET))
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
        void session으로_기존_로그인을_확인하고_FOUND로_리다이렉트할_URL을_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET))
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
        void OK로_로그인_페이지를_응답한다() {
            // given
            when(mockHttpRequest.consistsOf(GET))
                    .thenReturn(true);

            // when
            loginController.service(mockHttpRequest, mockHttpResponse);

            // then
            verify(mockHttpResponse, times(1)).setResponseResource(OK, "/login.html");
        }
    }

    @Test
    void 다른_요청이면_예외를_던진다() {
        // given
        when(mockHttpRequest.consistsOf(any(HttpMethod.class)))
                .thenReturn(false);

        // when
        assertThatThrownBy(() -> loginController.service(mockHttpRequest, mockHttpResponse))
                .isInstanceOf(NotFoundException.class);
    }
}
