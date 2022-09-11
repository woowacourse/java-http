package nextstep.jwp.presentation.exceptionresolver;

import static nextstep.jwp.presentation.ResourceLocation.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotFoundExceptionResolverTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private NotFoundExceptionResolver notFoundExceptionResolver;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.notFoundExceptionResolver = new NotFoundExceptionResolver();
    }

    @Test
    void Not_Found_페이지로_리다이렉트한다() {
        // given, when
        notFoundExceptionResolver.resolveException(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.FOUND),
                () -> verify(httpResponse).setLocation(NOT_FOUND.getLocation())
        );
    }
}
