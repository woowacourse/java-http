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

import nextstep.jwp.application.MemberService;
import nextstep.jwp.exception.DuplicateAccountException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterRequestHandlerTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private MemberService memberService;
    private RegisterRequestHandler registerRequestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.memberService = mock(MemberService.class);
        this.registerRequestHandler = new RegisterRequestHandler(memberService);
    }

    @Test
    void 회원가입에_성공하면_index_페이지로_리다이렉트한다() {
        // given
        given(httpRequest.getRequestBody()).willReturn("mock=mock");

        // when
        final String viewName = registerRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(ROOT.getLocation()),
                () -> assertThat(viewName).isNull()
        );
    }

    @Test
    void 회원가입에_실패하면_예외를_던진다() {
        // given
        given(httpRequest.getRequestBody()).willReturn("mock=mock");
        willThrow(DuplicateAccountException.class)
                .given(memberService)
                .register(any());

        // when, then
        assertThatThrownBy(() -> registerRequestHandler.handle(httpRequest, httpResponse))
                .isInstanceOf(DuplicateAccountException.class);
    }
}
