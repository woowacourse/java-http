package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.line.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
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
import org.junit.jupiter.api.Test;
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

    @Test
    void uri가_일치하는_POST_요청일_때_body_값으로_회원가입하면_쿠키에_세션을_넣고_FOUND로_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(POST, "/register"))
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
    void uri가_일치하는_GET_요청일_때_OK로_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(GET, "/register"))
                .thenReturn(true);

        // when
        registerController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpResponse, times(1)).setResponseResource(OK, "/register.html");
    }
}
