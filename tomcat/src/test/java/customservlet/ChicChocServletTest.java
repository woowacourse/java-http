package customservlet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import customservlet.exception.NotFoundHandlerException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChicChocServletTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private ChicChocServlet chicChocServlet;
    private ViewResolver viewResolver;
    private MappedRequestHandlers mappedRequestHandlers;
    private MappedExceptionResolvers mappedExceptionResolvers;
    private RequestHandler requestHandler;
    private ExceptionResolver exceptionResolver;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.httpResponse = mock(HttpResponse.class);
        this.httpRequest = mock(HttpRequest.class);
        this.viewResolver = mock(ViewResolver.class);
        this.mappedRequestHandlers = mock(MappedRequestHandlers.class);
        this.mappedExceptionResolvers = mock(MappedExceptionResolvers.class);
        this.chicChocServlet = new ChicChocServlet(viewResolver, mappedRequestHandlers, mappedExceptionResolvers);
        this.requestHandler = mock(RequestHandler.class);
        this.exceptionResolver = mock(ExceptionResolver.class);
    }

    @Test
    void RequestHandler가_요청을_처리한다() {
        // given
        given(mappedRequestHandlers.getHandler(httpRequest)).willReturn(requestHandler);
        given(requestHandler.handle(httpRequest, httpResponse)).willReturn("viewName");

        // when
        chicChocServlet.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(mappedRequestHandlers).getHandler(httpRequest),
                () -> verify(viewResolver).resolve("viewName", httpResponse)
        );
    }

    @Test
    void 예외가_발생하면_ExceptionResolver가_예외를_처리한다() {
        // given
        final var notFoundHandlerException = new NotFoundHandlerException();
        willThrow(notFoundHandlerException)
                .given(mappedRequestHandlers)
                .getHandler(httpRequest);
        given(mappedExceptionResolvers.getResolver(notFoundHandlerException))
                .willReturn(exceptionResolver);

        // when
        chicChocServlet.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(mappedExceptionResolvers).getResolver(notFoundHandlerException),
                () -> verify(exceptionResolver).resolveException(httpRequest, httpResponse)
        );
    }
}
