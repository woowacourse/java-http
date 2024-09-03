package org.apache.coyote.http11.method;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetMethodHandlerTest {

    @Test
    @DisplayName("인덱스 페이지 요청을 처리한다.")
    void handleIndexPage() throws IOException {
        GetMethodHandler getMethodHandler = new GetMethodHandler();
        String requestLine = "GET /index.html HTTP/1.1";
        HttpRequest httpRequest = new HttpRequest(requestLine);

        HttpResponse httpResponse = getMethodHandler.handle(httpRequest);

        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("static 파일 요청을 처리한다.")
    void handleStaticFile() throws IOException {
        GetMethodHandler getMethodHandler = new GetMethodHandler();
        String requestLine = "GET /static/login.html HTTP/1.1";
        HttpRequest httpRequest = new HttpRequest(requestLine);

        HttpResponse httpResponse = getMethodHandler.handle(httpRequest);

        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
