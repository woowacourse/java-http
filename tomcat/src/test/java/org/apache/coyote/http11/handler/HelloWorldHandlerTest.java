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
class HelloWorldHandlerTest {

    private HelloWorldHandler helloWorldHandler = new HelloWorldHandler();

    @Mock
    private HttpRequest request;

    @Mock
    private RequestUri requestUri;

    @Test
    void 루트_경로_요청을_처리할_수_있다() {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/");

        // when
        boolean canHandle = helloWorldHandler.canHandle(request);

        // then
        assertThat(canHandle).isTrue();
    }

    @Test
    void 루트_경로가_아닌_요청은_처리할_수_없다() {
        // given
        when(request.getRequestPath()).thenReturn(requestUri);
        when(requestUri.getPath()).thenReturn("/other");

        // when
        boolean canHandle = helloWorldHandler.canHandle(request);

        // then
        assertThat(canHandle).isFalse();
    }

    @Test
    void 요청을_처리하고_Hello_world_응답을_반환한다() throws IOException {
        // when
        HttpResponse response = helloWorldHandler.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo("Hello world!")
        );
    }
}
