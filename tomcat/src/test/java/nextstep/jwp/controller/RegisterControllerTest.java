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
class RegisterControllerTest {

    @Mock
    private HttpRequest mockHttpRequest;

    @Mock
    private HttpResponse mockHttpResponse;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RegisterController registerController;

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
        boolean actual = registerController.canProcess(mockHttpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void POST_요청일_때_body_값으로_회원가입하면_쿠키에_세션을_넣고_FOUND로_리다이렉트할_URL을_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(POST))
                .thenReturn(true);
        when(authService.register(any(), any(), any()))
                .thenReturn("sessionId");

        // when
        registerController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(authService, times(1)).register(any(), any(), any());
        InOrder inOrder = Mockito.inOrder(mockHttpResponse);
        inOrder.verify(mockHttpResponse, times(1)).setResponseRedirect(FOUND, "/index.html");
        inOrder.verify(mockHttpResponse, times(1)).setResponseHeader(SET_COOKIE, "JSESSIONID=sessionId");
    }

    @Test
    void GET_요청일_때_OK로_회원가입_페이지를_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(GET))
                .thenReturn(true);

        // when
        registerController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpResponse, times(1)).setResponseResource(OK, "/register.html");
    }

    @Test
    void 다른_요청이면_예외를_던진다() {
        // given
        when(mockHttpRequest.consistsOf(any(HttpMethod.class)))
                .thenReturn(false);

        // when
        assertThatThrownBy(() -> registerController.service(mockHttpRequest, mockHttpResponse))
                .isInstanceOf(NotFoundException.class);
    }
}
