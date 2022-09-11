package customservlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import customservlet.exception.NotFoundHandlerException;
import org.apache.coyote.http11.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MappedRequestHandlersTest {

    private HttpRequest httpRequest;
    private MappedRequestHandlers mappedRequestHandlers;
    private RequestHandlerInfo requestHandlerInfo;
    private RequestHandler requestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = mock(HttpRequest.class);
        this.mappedRequestHandlers = MappedRequestHandlers.getInstance();
        this.requestHandlerInfo = mock(RequestHandlerInfo.class);
        this.requestHandler = mock(RequestHandler.class);
    }

    @Test
    void 요청값에_해당하는_핸들러를_반환한다() {
        // given
        given(requestHandlerInfo.canSupport(httpRequest)).willReturn(true);
        mappedRequestHandlers.addRequestHandler(requestHandlerInfo, requestHandler);

        // when
        final RequestHandler actual = mappedRequestHandlers.getHandler(httpRequest);

        // then
        assertThat(actual).isEqualTo(requestHandler);
    }

    @Test
    void 요청값에_해당하는_핸들러가_없으면_예외가_발생한다() {
        // given
        mappedRequestHandlers.addRequestHandler(requestHandlerInfo, requestHandler);

        // when, then
        assertThatThrownBy(() -> mappedRequestHandlers.getHandler(httpRequest))
                .isInstanceOf(NotFoundHandlerException.class);
    }
}
