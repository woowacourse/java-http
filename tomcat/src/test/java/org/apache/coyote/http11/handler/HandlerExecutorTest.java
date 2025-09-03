package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandlerExecutorTest {

    private HandlerExecutor handlerExecutor = new HandlerExecutor();

    @Mock
    private HttpRequest request;

    @Mock
    private RequestUri requestUri;

    @Test
    void 루트_경로_요청시_HelloWorldHandler가_처리한다() throws IOException {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/");

        // when
        HttpResponse response = handlerExecutor.execute(request);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo("Hello world!")
        );
    }

    @Test
    void 정적_파일_요청시_StaticFileHandler가_처리한다() throws IOException {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/index.html");

        // when
        HttpResponse response = handlerExecutor.execute(request);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody().toText()).contains("Test Page")
        );
    }

    @Test
    void 처리할_수_없는_요청시_NotFoundHandler가_처리한다() throws IOException {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/non-existent");

        // when
        HttpResponse response = handlerExecutor.execute(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }
}
