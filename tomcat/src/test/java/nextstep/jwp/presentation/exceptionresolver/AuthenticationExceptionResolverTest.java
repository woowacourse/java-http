package nextstep.jwp.presentation.exceptionresolver;

import static nextstep.jwp.presentation.ResourceLocation.UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthenticationExceptionResolverTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private AuthenticationExceptionResolver authenticationExceptionResolver;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.authenticationExceptionResolver = new AuthenticationExceptionResolver();
    }

    @Test
    void Unauthorized_페이지로_리다이렉트한다() {
        // given, when
        authenticationExceptionResolver.resolveException(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(UNAUTHORIZED.getLocation())
        );
    }
}
