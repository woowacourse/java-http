package org.apache.coyote.handler.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InternalServerErrorHandlerTest {

    @Test
    @DisplayName("모든 요청 처리: 500 응답 반환")
    void handle() {
        final RequestLine requestLine = new RequestLine("GET", "/index.html", "HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        final HttpResponse httpResponse = HttpResponse.builder().internalServerErrorBuild();
        assertThat(InternalServerErrorHandler.getInstance().handle(httpRequest)).isEqualTo(httpResponse);
    }
}
