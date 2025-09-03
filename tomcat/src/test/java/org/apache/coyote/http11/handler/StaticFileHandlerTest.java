package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticFileHandlerTest {

    @InjectMocks
    private StaticFileHandler staticFileHandler;

    @Mock
    private HttpRequestHandler notFoundHandler;

    @Mock
    private HttpRequest request;

    @Mock
    private RequestUri requestUri;

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/style.css", "/script.js"})
    void 정적_파일_요청을_처리할_수_있다(String path) {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn(path);

        // when
        boolean canHandle = staticFileHandler.canHandle(request);

        // then
        assertThat(canHandle).isTrue();
    }

    @Test
    void 정적_파일이_아닌_요청은_처리할_수_없다() {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/other");

        // when
        boolean canHandle = staticFileHandler.canHandle(request);

        // then
        assertThat(canHandle).isFalse();
    }

    @Test
    void 존재하는_정적_파일_요청을_처리한다() throws IOException {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/test.html");

        // when
        HttpResponse response = staticFileHandler.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo("<html>test</html>"),
                () -> assertThat(response.getHeaders().get("Content-Type")).contains("text/html;charset=utf-8")
        );
    }

    @Test
    void 존재하지_않는_정적_파일_요청시_notFoundHandler를_호출한다() throws IOException {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/non-existent.html");

        // when
        staticFileHandler.handle(request);

        // then
        verify(notFoundHandler).handle(request);
    }
}
