package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


class NotFoundHandlerTest {

    private NotFoundHandler notFoundHandler = new NotFoundHandler();

    @Mock
    private HttpRequest request;

    @Test
    void 기본적으로_요청을_처리할_수_없다() {
        // when
        boolean canHandle = notFoundHandler.canHandle(request);

        // then
        assertThat(canHandle).isFalse();
    }

    @Test
    void 요청을_처리하고_404_응답을_반환한다() throws IOException {
        // when
        HttpResponse response = notFoundHandler.handle(request);

        // then
        InputStream is = getClass().getClassLoader().getResourceAsStream("static/404.html");
        byte[] expectedContent = is.readAllBytes();
        is.close();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo(new String(expectedContent))
        );
    }
}
