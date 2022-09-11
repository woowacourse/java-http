package nextstep.jwp.presentation.exceptionresolver;

import static nextstep.jwp.presentation.ResourceLocation.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BadRequestExceptionResolverTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private BadRequestExceptionResolver badRequestExceptionResolver;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.badRequestExceptionResolver = new BadRequestExceptionResolver();
    }

    @Test
    void Bad_Request_페이지로_리다이렉트한다() {
        // given, when
        badRequestExceptionResolver.resolveException(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(BAD_REQUEST.getLocation())
        );
    }
}
